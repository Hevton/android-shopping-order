package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.ui.model.OrderUiModel
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.shopping.ShoppingActivity

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {

    private val binding: ActivityOrderHistoryBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_order_history)
    }
    private val presenter: OrderHistoryContract.Presenter by lazy {
        OrderHistoryPresenter(
            view = this,
            repository = OrderRepositoryImpl(OrderRemoteDataSourceImpl())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        initToolbar()
        presenter.getOrders()
    }

    private fun initToolbar() {
        binding.tbOrders.setNavigationOnClickListener {
            finish()
        }
    }

    override fun initView(orders: List<OrderUiModel>) {
        runOnUiThread {
            binding.rvOrders.adapter = OrderHistoryAdapter(
                orders = orders,
                onClicked = ::showOrderDetail
            )
        }
    }

    override fun showErrorMessage(errorMessage: String) {
        val intent: Intent = ShoppingActivity.getIntent(this)

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    private fun showOrderDetail(order: OrderUiModel) {
        val intent: Intent = OrderDetailActivity.getIntent(
            context = this,
            order = order
        )

        startActivity(intent)
    }

    companion object {
        fun getIntent(context: Context): Intent {

            return Intent(context, OrderHistoryActivity::class.java)
        }
    }
}
