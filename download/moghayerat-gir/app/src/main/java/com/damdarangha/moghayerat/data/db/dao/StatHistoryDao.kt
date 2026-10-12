package com.damdarangha.moghayerat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.damdarangha.moghayerat.data.db.entity.StatHistoryEntity

@Dao
interface StatHistoryDao {

    @Insert
    suspend fun insert(history: StatHistoryEntity): Long

    @Query("SELECT * FROM stat_history WHERE statId = :statId ORDER BY changedAt DESC")
    suspend fun getHistoryForStat(statId: Long): List<StatHistoryEntity>

    @Query("SELECT * FROM stat_history WHERE productId = :productId ORDER BY changedAt DESC")
    suspend fun getHistoryForProduct(productId: Long): List<StatHistoryEntity>

    @Query("DELETE FROM stat_history WHERE statId = :statId")
    suspend fun deleteForStat(statId: Long)
}
