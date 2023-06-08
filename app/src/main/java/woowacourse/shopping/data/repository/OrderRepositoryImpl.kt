package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.order.OrderDataSource
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderErrorCode
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderErrorCode.LACK_OF_POINT
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderErrorCode.SHORTAGE_STOCK
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderFailureException
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.exception.AddOrderException.LackOfPointException
import woowacourse.shopping.domain.exception.AddOrderException.ShortageStockException
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(private val orderDataSource: OrderDataSource.Remote) : OrderRepository {
    override fun addOrder(
        basketProductsId: List<Int>,
        usingPoint: Int,
        orderTotalPrice: Int,
        onReceived: (Result<Int>) -> Unit
    ) {
        orderDataSource.addOrder(
            basketProductsId = basketProductsId,
            usingPoint = usingPoint,
            orderTotalPrice = orderTotalPrice,
            onReceived = { result ->
                result
                    .onSuccess { orderId ->
                        onReceived(Result.success(orderId))
                    }.onFailure {
                        val addOrderFailureException = it as AddOrderFailureException
                        val numberErrorCode = addOrderFailureException.addOrderErrorBody.errorCode
                        val errorMessage = addOrderFailureException.addOrderErrorBody.message
                        when (AddOrderErrorCode.getErrorCodeFromNumberCode(numberErrorCode)) {
                            SHORTAGE_STOCK -> {
                                onReceived(Result.failure(ShortageStockException(errorMessage)))
                            }
                            LACK_OF_POINT -> {
                                onReceived(Result.failure(LackOfPointException(errorMessage)))
                            }
                        }
                    }
            }
        )
    }

    override fun getIndividualOrderInfo(orderId: Int, onReceived: (Order) -> Unit) {
        orderDataSource.getIndividualOrderInfo(orderId) {
            onReceived(it.toDomain())
        }
    }

    override fun getOrdersInfo(onReceived: (List<Order>) -> Unit) {
        orderDataSource.getOrdersInfo { ordersInfo ->
            onReceived(ordersInfo.map { it.toDomain() })
        }
    }
}
