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

    // ── Roadmap Items (Child Sub-tasks) ─────────────────

    fun getItemsForGoal(goalId: String): Flow<List<com.example.lifeeasy.data.local.entity.RoadmapItemEntity>> {
        return roadmapDao.getItemsForGoal(goalId)
    }

    suspend fun addItem(item: com.example.lifeeasy.data.local.entity.RoadmapItemEntity) {
        roadmapDao.insertItem(item)
    }

    suspend fun updateItem(item: com.example.lifeeasy.data.local.entity.RoadmapItemEntity) {
        roadmapDao.updateItem(item)
    }

    suspend fun deleteItem(item: com.example.lifeeasy.data.local.entity.RoadmapItemEntity) {
        roadmapDao.deleteItem(item)
    }
}
