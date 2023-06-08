package woowacourse.shopping.ui.shopping

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.model.UiPrice
import woowacourse.shopping.ui.model.UiProduct
import woowacourse.shopping.ui.model.UiRecentProduct

class ShoppingPresenterTest() {
    private lateinit var view: ShoppingContract.View
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private lateinit var basketRepository: BasketRepository
    private lateinit var presenter: ShoppingPresenter

    @Before
    fun initPresenter() {
        view = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        basketRepository = mockk(relaxed = true)

        // 기본 basket 더미데이터
        every { basketRepository.getAll(any()) } answers {
            val callback: (List<BasketProduct>) -> Unit = arg(0)
            callback(listOf())
        }

        presenter =
            ShoppingPresenter(view, productRepository, recentProductRepository, basketRepository)
    }

    @Test
    fun `장바구니 데이터를 불러오면 화면이 업데이트 된다`() {
        // given
        clearMocks(view)
        every { view.updateTotalBasketCount(any()) } just runs
        every { view.updateProducts(any()) } just runs

        // when
        presenter.updateBasket()

        // then
        verify(exactly = 1) { view.updateTotalBasketCount(any()) }
        verify(exactly = 1) { view.updateProducts(any()) }
    }

    @Test
    fun `최근 본 상품 목록을 업데이트 하면 화면 업데이트 로직도 호출된다`() {
        // given
        clearMocks(view)
        every { view.updateRecentProducts(any()) } just runs
        every { recentProductRepository.getPartially(any()) } returns listOf(
            RecentProduct(
                1,
                Product(1, "더미입니다만", Price(1000), "url")
            )
        )
        // when
        presenter.fetchRecentProducts()

        // then
        verify(exactly = 1) { view.updateRecentProducts(any()) }
    }

    @Test
    fun `장바구니를 업데이트하면 관련 데이터인 상품이 장바구니에 담긴 갯수 전체 장바구니 count 수가 업데이트 된다`() {
        // given
        clearMocks(view)
        every { view.updateProducts(any()) } just runs
        every { view.updateTotalBasketCount(any()) } just runs

        every { basketRepository.getAll(any()) } answers {
            val callback: (List<BasketProduct>) -> Unit = arg(0)
            callback(listOf())
        }

        every { productRepository.getPartially(any(), any(), any()) } answers {
            val callback: (List<BasketProduct>) -> Unit = arg(2)
            callback(listOf())
        }

        // when
        presenter.initBasket()

        // then
        verify(exactly = 1) { view.updateProducts(any()) }
        verify(exactly = 1) { view.updateTotalBasketCount(any()) }
    }

    @Test
    fun `장바구니 물품 총 갯수를 계산하고 뷰에 업데이트 한다`() {
        // given
        clearMocks(view)
        every { view.updateTotalBasketCount(any()) } just runs

        // when
        presenter.fetchTotalBasketCount()
        // then
        verify(exactly = 1) { view.updateTotalBasketCount(any()) }
    }

    @Test
    fun `장바구니에 이미 존재하는 물품을 더하면 데이터베이스 혹은 서버에 저장하고 관련 데이터(상품이 장바구니에 담긴 갯수 전체 장바구니 count 수)를 업데이트 한다`() {
        // given
        every { basketRepository.getAll(any()) } answers {
            val callback: (List<BasketProduct>) -> Unit = arg(0)
            callback(listOf(BasketProduct(1, Count(2), Product(1, "더미입니다만", Price(1), "url"))))
        }

        presenter =
            ShoppingPresenter(view, productRepository, recentProductRepository, basketRepository)

        clearMocks(view)
        every { view.updateProducts(any()) } just runs
        every { view.updateTotalBasketCount(any()) } just runs
        every { basketRepository.update(any()) } just runs
        // when
        presenter.plusBasketProductCount(Product(1, "더미입니다만", Price(1), "url"))
        // then
        verify(exactly = 1) { view.updateProducts(any()) }
        verify(exactly = 1) { view.updateTotalBasketCount(any()) }
        verify(exactly = 1) { basketRepository.update(any()) }
    }

    @Test
    fun `장바구니에 물품을 빼면 데이터베이스에서도 빼는 로직을 실행하고 관련 데이터(상품이 장바구니에 담긴 갯수 전체 장바구니 count 수)를 업데이트 한다`() {
        // given
        every { basketRepository.getAll(any()) } answers {
            val callback: (List<BasketProduct>) -> Unit = arg(0)
            callback(listOf(BasketProduct(1, Count(2), Product(1, "더미입니다만", Price(1), "url"))))
        }

        presenter =
            ShoppingPresenter(view, productRepository, recentProductRepository, basketRepository)

        clearMocks(view)
        every { view.updateProducts(any()) } just runs
        every { view.updateTotalBasketCount(any()) } just runs
        every { basketRepository.update(any()) } just runs
        // when
        presenter.minusBasketProductCount(Product(1, "더미입니다만", Price(1), "url"))
        // then
        verify(exactly = 1) { view.updateProducts(any()) }
        verify(exactly = 1) { view.updateTotalBasketCount(any()) }
        verify(exactly = 1) { basketRepository.update(any()) }
    }

    @Test
    fun `페이지네이션의 다음페이지가 존재여부를 더보기 버튼의 visibility를 업데이트 하기위해 전달한다`() {
        // given
        every { view.updateMoreButtonState(any()) } just runs
        // when
        presenter.fetchHasNext()
        // then
        verify(exactly = 1) { view.updateMoreButtonState(any()) }
    }

    @Test
    fun `상세조회 페이지로 넘어갈때 가장최근에 본상품을 다시 조회하면 그다음으로 최근에 본상품을 previousProduct로 전달한다`() {
        // given
        every { recentProductRepository.add(any()) } just runs
        val currentProductSlot = slot<UiProduct>()
        val previousProductSlot = slot<UiProduct>()
        every {
            view.showProductDetail(
                capture(currentProductSlot),
                any(),
                capture(previousProductSlot),
                any()
            )
        } just runs

        // when
        val recentProducts =
            List(4) { UiRecentProduct(it, UiProduct(it, "더미입니다만", UiPrice(1000), "url")) }
        presenter =
            ShoppingPresenter(
                view,
                productRepository,
                recentProductRepository,
                basketRepository,
                recentProducts = recentProducts
            )
        val selectedProducts = UiProduct(0, "더미입니다만", UiPrice(1000), "url")
        presenter.inquiryProductDetail(selectedProducts)

        // then
        assertEquals(currentProductSlot.captured, selectedProducts)
        assertEquals(previousProductSlot.captured, UiProduct(1, "더미입니다만", UiPrice(1000), "url"))
    }

    @Test
    fun `상세조회 페이지로 넘어갈때 가장최근에 본상품을 제외한 다른상품을 조회하면 가장 최근에 본상품을 previousProduct로 전달한다`() {
        // given
        every { recentProductRepository.add(any()) } just runs
        val currentProductSlot = slot<UiProduct>()
        val previousProductSlot = slot<UiProduct>()
        every {
            view.showProductDetail(
                capture(currentProductSlot),
                any(),
                capture(previousProductSlot),
                any()
            )
        } just runs

        // when
        val recentProducts =
            List(4) { UiRecentProduct(it, UiProduct(it, "더미입니다만", UiPrice(1000), "url")) }
        presenter =
            ShoppingPresenter(
                view,
                productRepository,
                recentProductRepository,
                basketRepository,
                recentProducts = recentProducts
            )
        val selectedProducts = UiProduct(3, "더미입니다만", UiPrice(1000), "url")
        presenter.inquiryProductDetail(selectedProducts)

        // then
        assertEquals(currentProductSlot.captured, selectedProducts)
        assertEquals(previousProductSlot.captured, UiProduct(0, "더미입니다만", UiPrice(1000), "url"))
    }

    @Test
    fun `장바구니에 없는 상품을 추가한다`() {
        // given
        every { basketRepository.getAll(any()) } answers {
            val callback: (List<BasketProduct>) -> Unit = arg(0)
            callback(listOf(BasketProduct(1, Count(2), Product(1, "더미입니다만", Price(1), "url"))))
        }

        presenter =
            ShoppingPresenter(view, productRepository, recentProductRepository, basketRepository)

        clearMocks(view)
        every { view.updateProducts(any()) } just runs
        every { view.updateTotalBasketCount(any()) } just runs
        every { basketRepository.add(any(), any()) } answers {
            val callback: (Int) -> Unit = arg(1)
            callback(2)
        }
        // when
        presenter.addBasketProduct(Product(2, "더미입니다만2", Price(30), "url"))
        // then
        verify(exactly = 1) { view.updateProducts(any()) }
        verify(exactly = 1) { view.updateTotalBasketCount(any()) }
        verify(exactly = 1) { basketRepository.add(any(), any()) }
    }

    @Test
    fun `제품 데이터를 업데이트 하면 skeleton 상태값과 제품 데이터값이 화면에서 최신화된다 `() {
        // given
        clearMocks(productRepository)
        clearMocks(view)
        every { view.updateProducts(any()) } just runs
        every { view.updateSkeletonState(any()) } just runs
        every { productRepository.getPartially(any(), any(), any()) } answers {
            val callback: (List<Product>) -> Unit = arg(2)
            callback(listOf())
        }
        // when
        presenter.updateProducts()
        // then
        verify(exactly = 1) { view.updateProducts(any()) }
        verify(exactly = 1) { view.updateSkeletonState(any()) }
        verify(exactly = 1) { productRepository.getPartially(any(), any(), any()) }
    }
}
