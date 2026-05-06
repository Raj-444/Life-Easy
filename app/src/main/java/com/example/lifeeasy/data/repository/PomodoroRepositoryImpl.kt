package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.PomodoroDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.PomodoroSession
import com.example.lifeeasy.domain.repository.PomodoroRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PomodoroRepositoryImpl @Inject constructor(
    private val pomodoroDao: PomodoroDao,
    private val firestore: FirebaseFirestore
) : PomodoroRepository {

    override fun getAllSessions(): Flow<List<PomodoroSession>> =
        pomodoroDao.getAllSessions().map { list -> list.map { it.toDomain() } }

    override fun getSessionsForRange(startDate: Long, endDate: Long): Flow<List<PomodoroSession>> =
        pomodoroDao.getSessionsForRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override suspend fun saveSession(session: PomodoroSession) {
        pomodoroDao.insert(session.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteSession(session: PomodoroSession) {
        pomodoroDao.delete(session.toEntity())
    }

    override suspend fun syncPendingPomodoroData() {
        val unsyncedSessions = pomodoroDao.getUnsyncedSessions()
        for (entity in unsyncedSessions) {
            try {
                firestore.collection("pomodoro_sessions").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                pomodoroDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                pomodoroDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
