package com.example.lifeeasy.data.local.dao

import androidx.room.*
import com.example.lifeeasy.data.local.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_items ORDER BY startTime ASC")
    fun getAllRoutineItems(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routine_items WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getRoutineItemsForDay(dayOfWeek: Int): Flow<List<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineItem(item: RoutineEntity)

    @Delete
    suspend fun deleteRoutineItem(item: RoutineEntity)
}
