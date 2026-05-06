package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    fun getAllSessions(): Flow<List<PomodoroSession>>
    fun getSessionsForRange(startDate: Long, endDate: Long): Flow<List<PomodoroSession>>
    suspend fun saveSession(session: PomodoroSession)
    suspend fun deleteSession(session: PomodoroSession)
    suspend fun syncPendingPomodoroData()
}
