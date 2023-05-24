package woowacourse.shopping.data.recentlyviewedproduct

import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentlyViewedProduct
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import java.time.LocalDateTime

class RecentlyViewedProductRepositoryImpl(
    private val recentlyViewedProductDataSource: RecentlyViewedProductDataSource,
    private val productDataSource: ProductDataSource
) : RecentlyViewedProductRepository {

    override fun save(
        product: Product,
        viewedTime: LocalDateTime,
        onFinish: (RecentlyViewedProduct) -> Unit
    ) {
        recentlyViewedProductDataSource.save(product, viewedTime, onFinish)
    }

    override fun findFirst10OrderByViewedTimeDesc(onFinish: (List<RecentlyViewedProduct>) -> Unit) {
        productDataSource.findAll { products ->
            recentlyViewedProductDataSource.findFirst10OrderByViewedTimeDesc { recentlyViewedProductEntities ->
                onFinish(
                    recentlyViewedProductEntities.mapNotNull { entity ->
                        val productMap = products.associateBy { it.id }
                        val product = productMap[entity.productId] ?: return@mapNotNull null
                        RecentlyViewedProduct(
                            product.id, product, LocalDateTime.parse(entity.viewedDateTime)
                        )
                    }
                )
            }
        }
    }
}
