package woowacourse.shopping.data.repository.remote

import com.example.domain.model.CustomError
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderPreview
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.data.service.order.OrderRemoteService

class OrderRepositoryImpl(private val service: OrderRemoteService) : OrderRepository {
    override fun addOrder(
        cartIds: List<Long>,
        totalPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        service.requestAddOrder(cartIds, totalPrice, onSuccess, onFailure)
    }

    override fun getAll(onSuccess: (orders: List<OrderPreview>) -> Unit, onFailure: (CustomError) -> Unit) {
        service.requestAll(
            onSuccess,
            onFailure,
        )
    }

    override fun getOrderDetail(
        orderId: Long,
        onSuccess: (orderDetail: OrderDetail) -> Unit,
        onFailure: (CustomError) -> Unit,
    ) {
        service.requestOrderDetail(orderId, onSuccess, onFailure)
    }
}
