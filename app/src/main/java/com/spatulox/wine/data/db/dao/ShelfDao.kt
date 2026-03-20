package com.spatulox.wine.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.spatulox.wine.data.db.entity.ShelfEntity
import com.spatulox.wine.data.db.entity.StockEntity
import com.spatulox.wine.domain.model.Shelf
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {

    @Query("SELECT * FROM shelf WHERE id = :id")
    suspend fun get(id: Int): ShelfEntity?

    @Query("SELECT * FROM shelf ORDER BY compartmentId, `order`")
    fun getAllShelves(): Flow<List<ShelfEntity>>

    @Query("""
    SELECT * FROM stock 
    WHERE shelfId = :shelfId 
    ORDER BY `shelfId`, col
""")
    fun getStockByShelf(shelfId: Int): Flow<List<StockEntity>>

    @Insert
    suspend fun insert(shelf: ShelfEntity): Long

    @Update
    suspend fun update(shelf: ShelfEntity): Int

    @Delete
    suspend fun delete(shelf: ShelfEntity)

    @Query("DELETE FROM shelf WHERE id= :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM shelf ORDER BY `order` ASC")
    fun getShelfStream(): Flow<List<ShelfEntity>>

    @Query("SELECT * FROM shelf WHERE compartmentId = :compartmentId")
    suspend fun getShelvesByCompartmentId(compartmentId: Int): List<ShelfEntity>

}