package woowacourse.shopping.feature.userInfo

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.OrderUiModel

class OrderHistoryViewHolder(
    private val binding: ItemOrderBinding,
    private val onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(order: OrderUiModel) {
        binding.order = order
        binding.orderItemLayout.setOnClickListener { onClick.invoke(order.orderId) }
    }
}
