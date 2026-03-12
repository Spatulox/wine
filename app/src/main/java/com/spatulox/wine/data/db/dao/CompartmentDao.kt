package com.spatulox.wine.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.Query;

import com.spatulox.wine.data.db.entity.CompartmentEntity;
import kotlinx.coroutines.flow.Flow

@Dao
interface CompartmentDao {

    @Insert
    suspend fun insert(comp: CompartmentEntity): Long

    @Query("SELECT * FROM compartment ORDER BY `order` ASC")
    fun getCompartmentStream(): Flow<List<CompartmentEntity>>
}