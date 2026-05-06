package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.HealthDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.HealthLog
import com.example.lifeeasy.domain.repository.HealthRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val healthDao: HealthDao,
    private val firestore: FirebaseFirestore
) : HealthRepository {

    override fun getAllLogs(): Flow<List<HealthLog>> =
        healthDao.getAllLogs().map { list -> list.map { it.toDomain() } }

    override fun getTodayLogsByType(type: String): Flow<List<HealthLog>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return healthDao.getTodayLogsByType(type, calendar.timeInMillis).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveLog(log: HealthLog) {
        healthDao.insert(log.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteLog(log: HealthLog) {
        healthDao.delete(log.toEntity())
    }

    override suspend fun syncPendingLogs() {
        val unsyncedLogs = healthDao.getUnsyncedLogs()
        for (entity in unsyncedLogs) {
            try {
                firestore.collection("health_logs").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                healthDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                healthDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
