package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.spatulox.wine.data.db.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY date DESC")
    suspend fun getHistory(): List<HistoryEntity>

    @Query("SELECT * FROM history WHERE id= :id")
    suspend fun getHistoryById(id: Int): HistoryEntity?

    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getHistoryStream(): Flow<List<HistoryEntity>>

    @Insert
    suspend fun insert(history: HistoryEntity): Long
}