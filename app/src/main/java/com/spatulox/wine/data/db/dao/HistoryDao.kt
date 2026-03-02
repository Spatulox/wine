package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.spatulox.wine.data.db.entity.HistoryEntity

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY date DESC")
    suspend fun getHistory(): List<HistoryEntity>

    @Query("SELECT * FROM history WHERE id=id")
    suspend fun getHistoryById(id: Int): HistoryEntity

    @Insert
    suspend fun insert(history: HistoryEntity)
}