package woowacourse.shopping.data.remote.datasource.product

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.ProductRequest
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.data.remote.dto.response.ProductsResponse
import woowacourse.shopping.data.remote.service.ProductApi

class DefaultProductDataSource(
    private val productApi: ProductApi = ProductApi.service()
): ProductDataSource {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
    ): Response<ProductsResponse> {
        return productApi.getProducts(category = category, page = page, size = size)
    }

    override suspend fun postProduct(productRequest: ProductRequest): Response<Unit> {
        return productApi.postProduct(productRequest = productRequest)
    }

    override suspend fun getProductById(id: Int): Response<ProductResponse> {
        return productApi.getProductById(id = id)
    }

    override suspend fun deleteProductById(id: Int): Response<Unit> {
        return productApi.deleteProductById(id = id)
    }
}