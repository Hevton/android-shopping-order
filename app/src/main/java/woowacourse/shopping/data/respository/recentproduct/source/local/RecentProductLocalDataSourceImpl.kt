package woowacourse.shopping.data.respository.recentproduct.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import woowacourse.shopping.data.database.RecentProductContract
import woowacourse.shopping.data.database.RecentProductHelper
import woowacourse.shopping.data.database.getTableName
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.model.entity.RecentProductEntity
import java.time.LocalDateTime

class RecentProductLocalDataSourceImpl(
    context: Context,
    url: Server.Url,
) : RecentProductLocalDataSource {
    private val db = RecentProductHelper(context)
    private val tableName = getTableName(url)

    override fun insertRecentProduct(productId: Long) {
        val db = this.db.writableDatabase

        val value = ContentValues().apply {
            put(RecentProductContract.RecentProduct.PRODUCT_ID, productId)
            put(RecentProductContract.RecentProduct.CREATE_DATE, LocalDateTime.now().toString())
        }
        if (checkRecentProduct(productId)) {
            db.update(
                tableName,
                value,
                "${RecentProductContract.RecentProduct.PRODUCT_ID} = ? ",
                arrayOf(productId.toString()),
            )
            db.close()
            return
        }
        db.insert(tableName, null, value)

        db.close()
    }

    private fun checkRecentProduct(selectProductId: Long): Boolean {
        val recentProduct = selectRecentProductByProductId(selectProductId)
        if (recentProduct.isEmpty()) return false
        return true
    }

    private fun selectRecentProductByProductId(selectProductId: Long): List<RecentProductEntity> {
        val result = mutableListOf<RecentProductEntity>()
        val cursor = getCursorByProductId(selectProductId, 1)

        with(cursor) {
            while (moveToNext()) {
                val recentProductId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val productId =
                    getLong(getColumnIndexOrThrow(RecentProductContract.RecentProduct.PRODUCT_ID))
                result.add(RecentProductEntity(recentProductId, productId))
            }
        }
        cursor.close()

        return result
    }

    override fun deleteNotToday(today: String) {
        val db = this.db.writableDatabase
        val sql =
            "DELETE FROM $tableName WHERE ${RecentProductContract.RecentProduct.CREATE_DATE} NOT LIKE '$today%'"

        db.execSQL(sql)
        db.close()
    }

    override fun getAllRecentProducts(limit: Int): List<RecentProductEntity> {
        return getAllRecentProductsIds(limit)
    }

    private fun getAllRecentProductsIds(limit: Int): List<RecentProductEntity> {
        val result = mutableListOf<RecentProductEntity>()
        val cursor = getCursor(limit)
        with(cursor) {
            while (moveToNext()) {
                val recentProductId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val productId =
                    getLong(getColumnIndexOrThrow(RecentProductContract.RecentProduct.PRODUCT_ID))
                result.add(RecentProductEntity(recentProductId, productId))
            }
        }
        cursor.close()
        if (result.isEmpty()) result.add(getErrorData())

        return result.toList()
    }

    private fun getCursor(limit: Int): Cursor {
        val db = this.db.readableDatabase
        return db.query(
            tableName,
            null,
            null,
            null,
            null,
            null,
            RecentProductContract.RecentProduct.CREATE_DATE + " DESC",
            limit.toString(),
        )
    }

    private fun getCursorByProductId(productId: Long, limit: Int): Cursor {
        val db = this.db.readableDatabase
        return db.query(
            tableName,
            null,
            "${RecentProductContract.RecentProduct.PRODUCT_ID} = ?",
            arrayOf(productId.toString()),
            null,
            null,
            null,
            limit.toString(),
        )
    }

    private fun getErrorData() = RecentProductEntity(
        id = -1L,
        productId = -1L,
    )
}
