package woowacourse.shopping.ui.paymentconfirm

import woowacourse.shopping.ui.model.UiOrder
import woowacourse.shopping.ui.model.UiUserPointInfo
import woowacourse.shopping.ui.model.preorderinfo.UiPreOrderInfo

interface PaymentConfirmContract {
    interface View {
        fun updateUserPointInfo(userPointInfo: UiUserPointInfo)

        fun updatePreOrderInfo(preOrderInfo: UiPreOrderInfo)

        fun updatePointMessageCode(pointMessageCode: ApplyPointMessageCode)

        fun updateUsingPoint(usingPoint: Int)

        fun updateActualPayment(actualPayment: Int)

        fun showOrderSuccessNotification(orderInfo: UiOrder)

        fun showOrderLackOfPointFailureNotification(errorMessage: String)

        fun showOrderShortageStockFailureNotification(errorMessage: String)
    }

    interface Presenter {
        val view: View

        fun fetchUserPointInfo()

        fun fetchPreOrderInfo()

        fun applyPoint(input: Int)

        fun addOrder()
    }
}
