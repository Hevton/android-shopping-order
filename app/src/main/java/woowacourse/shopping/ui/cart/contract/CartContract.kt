package woowacourse.shopping.ui.cart.contract

import woowacourse.shopping.model.CartItemsUIModel
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.model.CartUIModel
import woowacourse.shopping.model.ProductUIModel

interface CartContract {

    interface View {
        fun setCarts(products: List<CartProductUIModel>, cartUIModel: CartUIModel)
        fun navigateToItemDetail(product: ProductUIModel)
        fun setCartItemsPrice(price: Int)
        fun setAllCheckbox(isChecked: Boolean)
        fun setAllOrderCount(count: Int)
        fun updateItem(id: Long, count: Int)
        fun updateChecked(id: Long, checked: Boolean)
        fun navigateToOrder(cartItems: CartItemsUIModel)
    }

    interface Presenter {
        fun setUpCarts()
        fun pageUp()
        fun pageDown()
        fun removeItem(id: Long)
        fun navigateToItemDetail(id: Long)

        fun saveOffsetState(outState: MutableMap<String, Int>)

        fun restoreOffsetState(state: Map<String, Int>)

        fun onCheckChanged(id: Long, isChecked: Boolean)

        fun setCartItemsPrice()
        fun onAllCheckboxClick(isChecked: Boolean)
        fun setAllCheckbox()
        fun setAllOrderCount()
        fun increaseCount(id: Long)
        fun decreaseCount(id: Long)
        fun navigateToOrder()
    }
}
