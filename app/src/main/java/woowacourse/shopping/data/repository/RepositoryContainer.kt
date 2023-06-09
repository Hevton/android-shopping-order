package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.local.LocalDataSourceContainer
import woowacourse.shopping.data.remote.RemoteDataSourceContainer

class RepositoryContainer private constructor(context: Context) {
    private val localDataSourceContainer = LocalDataSourceContainer(context)
    private val remoteDataSourceContainer = RemoteDataSourceContainer()

    val productRepository by lazy { DefaultProductRepository(remoteDataSourceContainer.product) }
    val recentlyViewedProductRepository by lazy {
        DefaultRecentlyViewedProductRepository(
            localDataSourceContainer.recentlyViewedProduct, remoteDataSourceContainer.product
        )
    }
    val userRepository by lazy {
        DefaultUserRepository(
            localDataSourceContainer.user, remoteDataSourceContainer.user
        )
    }
    val cartItemRepository by lazy {
        DefaultCartItemRepository(
            userRepository, remoteDataSourceContainer.cart
        )
    }
    val orderRepository by lazy {
        DefaultOrderRepository(
            userRepository, remoteDataSourceContainer.order
        )
    }

    companion object {
        private lateinit var instance: RepositoryContainer
        fun getInstance(context: Context): RepositoryContainer {
            if (!::instance.isInitialized) instance = RepositoryContainer(context)
            return instance
        }
    }
}
