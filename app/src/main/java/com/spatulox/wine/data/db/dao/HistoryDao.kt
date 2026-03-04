package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.spatulox.wine.data.db.entity.HistoryEntity
import com.spatulox.wine.data.db.entity.HistoryEntityWithWine
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY date DESC")
    suspend fun getHistory(): List<HistoryEntity>

    @Query("SELECT * FROM history WHERE id= :id")
    suspend fun getHistoryById(id: Int): HistoryEntity?

    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getHistoryStream(): Flow<List<HistoryEntity>>

    @Transaction
    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getHistoryWithWineStream(): Flow<List<HistoryEntityWithWine>>

    @Transaction
    @Query("SELECT * FROM history WHERE id= :id")
    suspend fun getHistoryWithWineById(id: Int): HistoryEntityWithWine?

    @Query("""
        SELECT DISTINCT date FROM history
    """)
    fun getHistoryYearStream(): Flow<List<Long>>

    @Insert
    suspend fun insert(history: HistoryEntity): Long
}