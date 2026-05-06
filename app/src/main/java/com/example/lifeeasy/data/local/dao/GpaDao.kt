package com.example.lifeeasy.data.local.dao

import androidx.room.*
import com.example.lifeeasy.data.local.entity.GpaResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpaDao {
    @Query("SELECT * FROM gpa_results ORDER BY timestamp DESC")
    fun getAllGpaResults(): Flow<List<GpaResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpaResult(result: GpaResultEntity)

    @Delete
    suspend fun deleteGpaResult(result: GpaResultEntity)
}
