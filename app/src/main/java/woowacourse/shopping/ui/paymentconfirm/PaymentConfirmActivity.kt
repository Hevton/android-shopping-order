package woowacourse.shopping.ui.paymentconfirm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.remote.RemoteOrderDataSource
import woowacourse.shopping.data.datasource.userpointdata.remote.RemoteUserPointInfoDataSource
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.PointRepositoryImpl
import woowacourse.shopping.databinding.ActivityPaymentConfirmBinding
import woowacourse.shopping.support.framework.presentation.editTextFocusOutProcess
import woowacourse.shopping.support.framework.presentation.getParcelableArrayListExtraCompat
import woowacourse.shopping.support.framework.presentation.intentDataNullProcess
import woowacourse.shopping.support.framework.presentation.setThrottleFirstOnClickListener
import woowacourse.shopping.ui.mapper.toDomain
import woowacourse.shopping.ui.model.UiBasketProduct
import woowacourse.shopping.ui.model.UiOrder
import woowacourse.shopping.ui.model.UiUserPointInfo
import woowacourse.shopping.ui.model.preorderinfo.UiPreOrderInfo
import woowacourse.shopping.ui.orderdetail.OrderDetailDialog
import woowacourse.shopping.ui.orderdetail.OrderDetailDialogFragmentFactory

class PaymentConfirmActivity : AppCompatActivity(), PaymentConfirmContract.View {

    private lateinit var binding: ActivityPaymentConfirmBinding
    private lateinit var presenter: PaymentConfirmContract.Presenter
    private lateinit var currentOrderBasketProducts: List<UiBasketProduct>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_confirm)
        if (!initExtraData()) return
        initPresenter()
        initUsePointButtonClickListener()
        initFinalOrderButtonClickListener()
        initCloseButtonClickListener()
    }

    private fun initExtraData(): Boolean {
        currentOrderBasketProducts =
            intent.getParcelableArrayListExtraCompat<UiBasketProduct>(CURRENT_ORDER_BASKET_PRODUCTS)
                ?.toList() ?: return intentDataNullProcess(CURRENT_ORDER_BASKET_PRODUCTS)
        return true
    }

    private fun initPresenter() {
        presenter = PaymentConfirmPresenter(
            this,
            PointRepositoryImpl(RemoteUserPointInfoDataSource()),
            OrderRepositoryImpl(RemoteOrderDataSource()),
            currentOrderBasketProducts.map { it.toDomain() }
        )
    }

    private fun initUsePointButtonClickListener() {
        binding.pointApplyClickListener = {
            if (it.toString().isNullOrBlank()) binding.etPaymentUsingPoint.setText(
                NO_INPUT_SETTING_POINT.toString()
            )
            val usePoint = if (it.toString().isNullOrBlank()) 0 else it.toString().toInt()
            presenter.applyPoint(usePoint)
        }
        binding.etPaymentUsingPoint.text.clear()
    }

    private fun initFinalOrderButtonClickListener() {
        binding.btnPaymentFinalOrder.setThrottleFirstOnClickListener { presenter.addOrder() }
    }

    private fun initCloseButtonClickListener() {
        binding.ivPaymentClose.setThrottleFirstOnClickListener {
            finish()
        }
    }

    override fun updateUserPointInfo(userPointInfo: UiUserPointInfo) {
        binding.userPointInfo = userPointInfo
    }

    override fun updatePreOrderInfo(preOrderInfo: UiPreOrderInfo) {
        binding.preOrderInfo = preOrderInfo
    }

    override fun updatePointMessageCode(pointMessageCode: ApplyPointMessageCode) {
        binding.applyPointMessageCode = pointMessageCode
    }

    override fun updateUsingPoint(usingPoint: Int) {
        binding.usingPoint = usingPoint
    }

    override fun updateActualPayment(actualPayment: Int) {
        binding.actualPayment = actualPayment
    }

    override fun showOrderSuccessNotification(orderInfo: UiOrder) {
        supportFragmentManager.fragmentFactory =
            OrderDetailDialogFragmentFactory(orderInfo) { finish() }
        val fragment: OrderDetailDialog = supportFragmentManager.fragmentFactory
            .instantiate(classLoader, OrderDetailDialog::class.java.name) as OrderDetailDialog
        fragment.show(supportFragmentManager, OrderDetailDialog::class.java.name)
    }

    override fun showOrderLackOfPointFailureNotification(errorMessage: String) {
        setAlertDialog(errorMessage) { presenter.applyPoint(0) }
    }

    override fun showOrderShortageStockFailureNotification(errorMessage: String) {
        setAlertDialog(errorMessage) { finish() }
    }

    private fun setAlertDialog(errorMessage: String, onClickListener: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(this.getString(R.string.FailureAddOrder))
            .setMessage(errorMessage)
            .setPositiveButton(
                this.getString(R.string.OrderFailureDialogPositiveButton)
            ) { _, _ -> onClickListener() }
            .create()
            .show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        this.editTextFocusOutProcess(ev)
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        private const val CURRENT_ORDER_BASKET_PRODUCTS = "currentOrderBasketProducts"
        private const val NO_INPUT_SETTING_POINT = 0

        fun getIntent(context: Context, basketProducts: List<UiBasketProduct>): Intent =
            Intent(context, PaymentConfirmActivity::class.java).apply {
                putParcelableArrayListExtra(
                    CURRENT_ORDER_BASKET_PRODUCTS,
                    ArrayList(basketProducts)
                )
            }
    }
}
