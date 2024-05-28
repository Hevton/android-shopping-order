package woowacourse.shopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.local.entity.RecentEntity
import woowacourse.shopping.data.local.entity.RecentProductEntity

@Dao
interface RecentProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecent(recentEntity: RecentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecentProduct(recentProductEntity: RecentProductEntity)

    @Query(
        "SELECT productId, name, imgUrl, price, createdAt " +
                "FROM recentproductentity " +
                "ORDER BY createdAt DESC LIMIT :limit",
    )
    fun findByLimit(limit: Int): List<RecentProductEntity>

    @Query(
        "SELECT productId, name, imgUrl, price, createdAt " +
            "FROM recentproductentity " +
            "ORDER BY createdAt DESC",
    )
    fun findOne(): RecentProductEntity?
}
