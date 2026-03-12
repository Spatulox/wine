package com.spatulox.wine.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query;
import androidx.room.Update

import com.spatulox.wine.data.db.entity.CompartmentEntity;
import kotlinx.coroutines.flow.Flow

@Dao
interface CompartmentDao {

    @Insert
    suspend fun insert(comp: CompartmentEntity): Long

    @Update
    suspend fun update(comp: CompartmentEntity)

    @Delete
    suspend fun delete(comp: CompartmentEntity)

    @Query("SELECT * FROM compartment ORDER BY `order` ASC")
    fun getCompartmentStream(): Flow<List<CompartmentEntity>>

}