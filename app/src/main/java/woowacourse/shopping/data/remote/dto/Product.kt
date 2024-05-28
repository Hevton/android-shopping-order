package woowacourse.shopping.data.remote.dto

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String
)
