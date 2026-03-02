package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spatulox.wine.data.db.entity.HistoryEntity
import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.data.db.entity.WineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WineDao {
    @Query("SELECT * FROM wine ORDER BY name ASC")
    suspend fun getWine(): List<WineEntity>

    @Query("SELECT * FROM wine WHERE id = :id")
    suspend fun getById(id: Int): WineEntity?

    @Query("SELECT * FROM wine WHERE year = :year ORDER BY name ASC")
    suspend fun getByYear(year: Int): List<WineEntity>

    @Query("SELECT * FROM wine")
    fun getWineStream(): Flow<List<WineEntity>>

    @Query("SELECT * FROM wine WHERE name LIKE '%' || :query || '%' OR year = :query")
    suspend fun search(query: String): List<WineEntity>

    @Insert
    suspend fun insert(wine: WineEntity): Long

    @Update
    suspend fun update(wine: WineEntity): Int

    @Delete
    suspend fun delete(wine: WineEntity)

    @Query("DELETE FROM wine WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM wine")
    suspend fun getCount(): Int
}