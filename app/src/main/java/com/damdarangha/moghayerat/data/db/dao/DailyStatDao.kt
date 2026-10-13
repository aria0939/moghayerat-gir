package com.damdarangha.moghayerat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.damdarangha.moghayerat.data.db.entity.DailyStatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatDao {

    @Query("SELECT * FROM daily_stats WHERE productId = :productId AND isoDate = :isoDate LIMIT 1")
    suspend fun getStat(productId: Long, isoDate: String): DailyStatEntity?

    @Query("SELECT * FROM daily_stats WHERE productId = :productId AND isoDate = :isoDate LIMIT 1")
    fun observeStat(productId: Long, isoDate: String): Flow<DailyStatEntity?>

    @Query("SELECT * FROM daily_stats WHERE isoDate = :isoDate ORDER BY productId ASC")
    fun observeStatsForDate(isoDate: String): Flow<List<DailyStatEntity>>

    @Query("SELECT * FROM daily_stats WHERE isoDate = :isoDate ORDER BY productId ASC")
    suspend fun getStatsForDate(isoDate: String): List<DailyStatEntity>

    @Query("""
        SELECT * FROM daily_stats
        WHERE productId = :productId
        AND isoDate < :isoDate
        ORDER BY isoDate DESC
        LIMIT 1
    """)
    suspend fun getPreviousDayStat(productId: Long, isoDate: String): DailyStatEntity?

    @Query("""
        SELECT * FROM daily_stats
        WHERE productId = :productId
        AND isoDate < :referenceDate
        ORDER BY isoDate DESC
        LIMIT 1
    """)
    suspend fun getLatestStatBeforeDate(productId: Long, referenceDate: String): DailyStatEntity?

    @Query("""
        SELECT * FROM daily_stats
        WHERE isoDate BETWEEN :startDate AND :endDate
        ORDER BY isoDate DESC, productId ASC
    """)
    fun observeStatsForDateRange(startDate: String, endDate: String): Flow<List<DailyStatEntity>>

    @Query("""
        SELECT * FROM daily_stats
        WHERE productId = :productId
        ORDER BY isoDate DESC
    """)
    fun observeProductHistory(productId: Long): Flow<List<DailyStatEntity>>

    @Query("SELECT * FROM daily_stats WHERE isoDate = :isoDate AND currentStockTotal IS NOT NULL AND closingVariance = 0")
    fun observeZeroVarianceStats(isoDate: String): Flow<List<DailyStatEntity>>

    @Query("SELECT * FROM daily_stats WHERE isoDate = :isoDate AND currentStockTotal IS NOT NULL AND closingVariance > 0")
    fun observeDeficitStats(isoDate: String): Flow<List<DailyStatEntity>>

    @Query("SELECT * FROM daily_stats WHERE isoDate = :isoDate AND currentStockTotal IS NOT NULL AND closingVariance < 0")
    fun observeSurplusStats(isoDate: String): Flow<List<DailyStatEntity>>

    @Query("SELECT COUNT(*) FROM daily_stats WHERE isoDate = :isoDate")
    suspend fun countStatsForDate(isoDate: String): Int

    @Query("SELECT COUNT(*) FROM daily_stats WHERE isoDate = :isoDate AND currentStockTotal IS NOT NULL AND closingVariance != 0")
    suspend fun countVarianceStatsForDate(isoDate: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: DailyStatEntity): Long

    @Update
    suspend fun update(stat: DailyStatEntity)

    @Query("UPDATE daily_stats SET isFinalized = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun finalize(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE daily_stats SET isFinalized = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun unfinalize(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM daily_stats WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM daily_stats")
    suspend fun getAll(): List<DailyStatEntity>
}
