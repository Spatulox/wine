package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.spatulox.wine.data.db.entity.WineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WineDao {
    @Query("SELECT * FROM wine WHERE id = :id")
    suspend fun getById(id: Int): WineEntity?

    @Query("SELECT * FROM wine ORDER BY name ASC")
    fun getWineStream(): Flow<List<WineEntity>>

    @Query("SELECT DISTINCT year FROM wine ORDER BY year ASC")
    fun getWineYearStream(): Flow<List<Int>>

    @Insert
    suspend fun insert(wine: WineEntity): Long

    @Query("UPDATE wine SET qte = qte - 1 WHERE id = :wineId AND qte > 0")
    suspend fun withdrawWine(wineId: Int)

    @Update
    suspend fun update(wine: WineEntity): Int

    @Delete
    suspend fun delete(wine: WineEntity)
}