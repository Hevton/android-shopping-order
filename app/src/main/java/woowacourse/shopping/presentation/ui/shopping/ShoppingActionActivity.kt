package woowacourse.shopping.presentation.ui.shopping

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.R
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.cart.CartActivity
import woowacourse.shopping.presentation.ui.detail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.shopping.adapter.RecentAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingViewType
import woowacourse.shopping.utils.getParcelableExtraCompat

class ShoppingActionActivity : BindingActivity<ActivityShoppingBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_shopping

    private val viewModel: ShoppingViewModel by viewModels { ViewModelFactory() }

    private val shoppingAdapter: ShoppingAdapter by lazy { ShoppingAdapter(viewModel) }
    private val recentAdapter: RecentAdapter by lazy { RecentAdapter(viewModel) }

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun initStartView() {
        initTitle()
        initAdapter()
        initData()
        initObserver()
        initLauncher()

        RetrofitModule.productApi.getProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("DATATA", "body : $body")
                }
            }
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                println("error : $t")
            }
        })
    }

    private fun initTitle() {
        supportActionBar?.hide()
    }

    private fun initLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getParcelableExtraCompat<UpdateUiModel>(
                        EXTRA_UPDATED_PRODUCT,
                    )
                        ?.let {
                            viewModel.updateCartProducts(it)
                        }
                }
                viewModel.findAllRecent()
            }
    }

    private fun initData() {
        viewModel.loadProductByOffset()
        viewModel.findAllRecent()
        viewModel.getItemCount()
    }

    private fun initObserver() {
        binding.shoppingActionHandler = viewModel
        viewModel.products.observe(this) {
            when (it) {
                is UiState.None -> {}
                is UiState.Success -> {
                    shoppingAdapter.submitList(it.data)
                }
            }
        }
        viewModel.cartCount.observe(this) {
            binding.tvCartCount.text = it.toString()
        }
        viewModel.recentProducts.observe(this) {
            when (it) {
                is UiState.None -> {}
                is UiState.Success -> {
                    recentAdapter.submitList(it.data) {
                        binding.rvRecents.scrollToPosition(0)
                    }
                }
            }
        }
        viewModel.errorHandler.observe(
            this,
            EventObserver {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
        )
        viewModel.navigateHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is NavigateUiState.ToDetail -> {
                        resultLauncher.launch(
                            ProductDetailActivity.createIntent(this, it.productId),
                        )
                    }
                    is NavigateUiState.ToCart -> {
                        resultLauncher.launch(
                            CartActivity.createIntent(this),
                        )
                    }
                }
            },
        )
    }

    private fun initAdapter() {
        val layoutManager = GridLayoutManager(this, GRIDLAYOUT_COL)
        layoutManager.spanSizeLookup = spanManager
        binding.rvShopping.layoutManager = layoutManager
        binding.rvShopping.adapter = shoppingAdapter
        binding.rvRecents.adapter = recentAdapter
    }

    private val spanManager =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (shoppingAdapter.getItemViewType(position) == ShoppingViewType.Product.value) {
                    ShoppingViewType.Product.span
                } else {
                    ShoppingViewType.LoadMore.span
                }
            }
        }

    companion object {
        const val GRIDLAYOUT_COL = 2
        const val EXTRA_UPDATED_PRODUCT = "updatedProduct"
    }
}
