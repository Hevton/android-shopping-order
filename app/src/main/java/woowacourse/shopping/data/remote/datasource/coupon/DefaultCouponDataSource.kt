package woowacourse.shopping.data.remote.datasource.coupon

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.response.CouponResponse
import woowacourse.shopping.data.remote.service.CouponApi

class DefaultCouponDataSource(
    private val couponApi: CouponApi = CouponApi.service()
): CouponDataSource {

    override suspend fun getCoupons(): Response<List<CouponResponse>> {
        return couponApi.getCoupons()
    }
}