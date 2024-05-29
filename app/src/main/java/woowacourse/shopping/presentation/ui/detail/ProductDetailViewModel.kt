package woowacourse.shopping.presentation.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import kotlin.concurrent.thread

class ProductDetailViewModel(
    private val repository: Repository,
) : ViewModel(), DetailActionHandler {
    private val _product = MutableLiveData<UiState<DetailCartProduct>>(UiState.Loading)
    val product: LiveData<UiState<DetailCartProduct>> get() = _product

    private val _recentProduct = MutableLiveData<UiState<RecentProduct>>(UiState.Loading)
    val recentProduct: LiveData<UiState<RecentProduct>> get() = _recentProduct

    private val _errorHandler = MutableLiveData<EventState<String>>()
    val errorHandler: LiveData<EventState<String>> get() = _errorHandler

    private val _cartHandler = MutableLiveData<EventState<UpdateUiModel>>()
    val cartHandler: LiveData<EventState<UpdateUiModel>> get() = _cartHandler

    private val _navigateHandler = MutableLiveData<EventState<Long>>()
    val navigateHandler: LiveData<EventState<Long>> get() = _navigateHandler

    private val updateUiModel: UpdateUiModel = UpdateUiModel()

//    fun findCartProductById(id: Long) {
//        thread {
//            repository.getProductById(id.toInt()).onSuccess {
//                if (it == null) {
//                    _errorHandler.postValue(EventState(PRODUCT_NOT_FOUND))
//                } else {
//                    _product.postValue(UiState.Success(it))
//                    saveRecentProduct(it)
//                }
//            }.onFailure {
//                _errorHandler.value = EventState(PRODUCT_NOT_FOUND)
//            }
//        }
//    }

    fun setCartProduct(cartProduct: CartProduct?) {
        if(cartProduct != null) {
            thread {
                saveRecentProduct(cartProduct)
            }
            val detailCartProduct = DetailCartProduct(
                isNew = cartProduct.quantity == 0,
                cartProduct = cartProduct.copy(
                    quantity = if(cartProduct.quantity == 0) 1 else cartProduct.quantity
                )
            )
            _product.value = UiState.Success(detailCartProduct)
        }
    }

    fun findOneRecentProduct() {
        thread {
            repository.findOne().onSuccess {
                if (it == null) {
                    _recentProduct.postValue(UiState.Loading)
                } else {
                    _recentProduct.postValue(UiState.Success(it))
                }
            }.onFailure {
                _errorHandler.postValue(EventState(PRODUCT_NOT_FOUND))
            }
        }
    }

    override fun onAddToCart(detailCartProduct: DetailCartProduct) {
        thread {
            updateUiModel.add(detailCartProduct.cartProduct.productId, detailCartProduct.cartProduct)

            if(detailCartProduct.isNew) {
                repository.postCartItem(
                    CartItemRequest(
                        productId = detailCartProduct.cartProduct.productId.toInt(),
                        quantity = detailCartProduct.cartProduct.quantity
                    )
                ).onSuccess {
                    _product.postValue(UiState.Success(detailCartProduct))
                    saveRecentProduct(detailCartProduct.cartProduct)
                }.onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
            } else {

                repository.patchCartItem(
                    id = detailCartProduct.cartProduct.cartId.toInt(),
                    quantityRequest = QuantityRequest(
                        detailCartProduct.cartProduct.quantity
                    )
                ).onSuccess {
                    _product.postValue(UiState.Success(detailCartProduct))
                    saveRecentProduct(detailCartProduct.cartProduct)
                }
                    .onFailure {
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
                    }
            }

            _cartHandler.postValue(EventState(updateUiModel))
        }
    }

    override fun onNavigateToDetail(productId: Long) {
        _navigateHandler.value = EventState(productId)
    }

    override fun onPlus(cartProduct: CartProduct) {
        thread {
            cartProduct.plusQuantity()
            _product.postValue(UiState.Success(
                (_product.value as UiState.Success).data.copy(cartProduct = cartProduct)
            ))
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        thread {
            cartProduct.minusQuantity()
            _product.postValue(UiState.Success(
                (_product.value as UiState.Success).data.copy(cartProduct = cartProduct)
            ))
        }
    }

    fun saveRecentProduct(cartProduct: CartProduct) {
        thread {
            repository.saveRecentProduct(
                RecentProduct(
                    cartProduct.productId,
                    cartProduct.name,
                    cartProduct.imgUrl,
                    cartProduct.quantity,
                    cartProduct.price,
                    System.currentTimeMillis(),
                    category = cartProduct.category,
                    cartId = cartProduct.cartId
                ),
            ).onSuccess {
            }.onFailure {
                _errorHandler.postValue(EventState("최근 아이템 추가 에러"))
            }
        }
    }

    companion object {
        const val PRODUCT_NOT_FOUND = "PRODUCT NOT FOUND"
    }
}
