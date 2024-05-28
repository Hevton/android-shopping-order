package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    val productId: Int,
    val quantity: Int
)