package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.spatulox.wine.data.db.entity.HistoryEntity

@Dao
interface WineDao {
    @Query("SELECT * FROM history ORDER BY date DESC")
    suspend fun getWine(): List<HistoryEntity>
}