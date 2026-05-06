package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.dao.RoutineDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.RoutineItem
import com.example.lifeeasy.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao
) : RoutineRepository {
    override fun getAllRoutineItems(): Flow<List<RoutineItem>> {
        return routineDao.getAllRoutineItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRoutineItemsForDay(dayOfWeek: Int): Flow<List<RoutineItem>> {
        return routineDao.getRoutineItemsForDay(dayOfWeek).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveRoutineItem(item: RoutineItem) {
        routineDao.insertRoutineItem(item.toEntity())
    }

    override suspend fun deleteRoutineItem(item: RoutineItem) {
        routineDao.deleteRoutineItem(item.toEntity())
    }
}
