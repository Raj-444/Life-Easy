package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.RoutineItem
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    fun getAllRoutineItems(): Flow<List<RoutineItem>>
    fun getRoutineItemsForDay(dayOfWeek: Int): Flow<List<RoutineItem>>
    suspend fun saveRoutineItem(item: RoutineItem)
    suspend fun deleteRoutineItem(item: RoutineItem)
}
