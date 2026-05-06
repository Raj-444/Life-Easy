package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.HealthLog
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getAllLogs(): Flow<List<HealthLog>>
    fun getTodayLogsByType(type: String): Flow<List<HealthLog>>
    suspend fun saveLog(log: HealthLog)
    suspend fun deleteLog(log: HealthLog)
    suspend fun syncPendingLogs()
}
