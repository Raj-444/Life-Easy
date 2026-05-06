package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.data.local.dao.RoadmapDao
import com.example.lifeeasy.data.local.entity.RoadmapEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoadmapRepository @Inject constructor(
    private val roadmapDao: RoadmapDao
) {
    fun getAllGoals(): Flow<List<RoadmapEntity>> {
        return roadmapDao.getAllGoals()
    }

    suspend fun addGoal(goal: RoadmapEntity) {
        roadmapDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: RoadmapEntity) {
        roadmapDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: RoadmapEntity) {
        roadmapDao.deleteGoal(goal)
    }
}
