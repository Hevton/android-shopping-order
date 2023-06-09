package woowacourse.shopping.data.remote.dto.response

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

data class ProductDto(
    val product: Product,
    val cartItem: CartItem?,
)
