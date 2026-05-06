package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeeasy.data.local.entity.RoadmapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadmapDao {
    @Query("SELECT * FROM roadmap_goals ORDER BY createdAt ASC")
    fun getAllGoals(): Flow<List<RoadmapEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: RoadmapEntity)

    @Update
    suspend fun updateGoal(goal: RoadmapEntity)

    @Delete
    suspend fun deleteGoal(goal: RoadmapEntity)
}
