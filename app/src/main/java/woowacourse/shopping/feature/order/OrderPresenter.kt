package woowacourse.shopping.feature.order

import com.example.domain.model.order.OrderDetailProduct
import com.example.domain.model.point.Point
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.PointRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toPresentation

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val pointRepository: PointRepository,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {

    private val orderProducts by lazy {
        val cartProducts = cartRepository.getAll()
        cartProducts.filter { it.isSelected }.map { it.toPresentation() }
    }

    override fun loadProducts() {
        view.initOrderProducts(orderProducts)
    }

    override fun loadPayment() {
        val sumOfProductPrice = orderProducts.sumOf { it.totalPrice() }
        pointRepository.getPoint(
            onSuccess = { view.setUpView(it.currentPoint, sumOfProductPrice) },
            onFailure = {
                view.showErrorMessage(Throwable("오류 발생"))
            }
        )
    }

    override fun validatePointCondition(inputValue: CharSequence?, point: Int) {
        val sumOfProductPrice = orderProducts.sumOf { it.totalPrice() }
        val inputPoint =
            if (inputValue.isNullOrBlank()) 0 else inputValue.toString().toInt()

        if (inputPoint > point) {
            view.overOwnPoint(sumOfProductPrice)
        } else {
            view.updateTotalPriceBtn(sumOfProductPrice - inputPoint)
        }
    }

    override fun orderProducts(usedPoint: Int) {
        val orders =
            orderProducts.map { OrderDetailProduct(it.count, it.productUiModel.toDomain()) }
        orderRepository.addOrder(
            Point(usedPoint), orders,
            callback = {
                it.onSuccess {
                    view.successOrder()
                }.onFailure { throwable ->
                    view.showErrorMessage(throwable)
                }
            }
        )
    }
}
