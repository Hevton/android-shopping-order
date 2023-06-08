package woowacourse.shopping.feature.detail

import woowacourse.shopping.model.ProductUiModel

interface DetailContract {
    interface View {
        fun showCartScreen()
        fun showSelectCountScreen(product: ProductUiModel)
        fun showFailureMessage(message: String)
    }

    interface Presenter {
        val product: ProductUiModel
        val count: Int
        fun increaseCount()
        fun decreaseCount()
        fun selectCount()
        fun addCart()
    }
}
