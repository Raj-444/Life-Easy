package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.CounterDao
import com.example.lifeeasy.data.local.dao.CounterHistoryDao
import com.example.lifeeasy.data.local.entity.CounterHistoryEntity
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.Counter
import com.example.lifeeasy.domain.model.CounterHistory
import com.example.lifeeasy.domain.repository.CounterRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CounterRepositoryImpl @Inject constructor(
    private val counterDao: CounterDao,
    private val counterHistoryDao: CounterHistoryDao,
    private val firestore: FirebaseFirestore
) : CounterRepository {

    override fun getAllCounters(): Flow<List<Counter>> =
        counterDao.getAllCounters().map { list -> list.map { it.toDomain() } }

    override suspend fun getCounterById(id: String): Counter? =
        counterDao.getCounterById(id)?.toDomain()

    override suspend fun saveCounter(counter: Counter) {
        val isNew = counter.id.isBlank() || counterDao.getCounterById(counter.id) == null
        val id = if (counter.id.isBlank()) java.util.UUID.randomUUID().toString() else counter.id
        val finalCounter = if (counter.id.isBlank()) counter.copy(id = id) else counter
        
        counterDao.insert(finalCounter.toEntity(SyncStatus.PENDING))
        
        if (isNew) {
            counterHistoryDao.insert(
                CounterHistoryEntity(
                    counterId = id,
                    oldCount = 0,
                    newCount = finalCounter.count,
                    changeType = "INITIAL",
                    syncStatus = SyncStatus.PENDING
                )
            )
        }
    }

    override suspend fun increment(id: String) {
        val current = counterDao.getCounterById(id) ?: return
        val newVal = current.count + 1
        counterDao.increment(id)
        counterHistoryDao.insert(
            CounterHistoryEntity(
                counterId = id,
                oldCount = current.count,
                newCount = newVal,
                changeType = "INCREMENT",
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    override suspend fun decrement(id: String) {
        val current = counterDao.getCounterById(id) ?: return
        val newVal = current.count - 1
        counterDao.decrement(id)
        counterHistoryDao.insert(
            CounterHistoryEntity(
                counterId = id,
                oldCount = current.count,
                newCount = newVal,
                changeType = "DECREMENT",
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    override suspend fun deleteCounter(counter: Counter) {
        counterDao.delete(counter.toEntity())
    }

    override fun getHistoryForCounter(counterId: String): Flow<List<CounterHistory>> =
        counterHistoryDao.getHistoryForCounter(counterId).map { list -> list.map { it.toDomain() } }

    override suspend fun syncPendingCounters() {
        // Sync Counters
        val unsyncedCounters = counterDao.getUnsyncedCounters()
        for (entity in unsyncedCounters) {
            try {
                firestore.collection("counters").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                counterDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                counterDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }

        // Sync History
        val unsyncedHistory = counterHistoryDao.getUnsyncedHistory()
        for (entity in unsyncedHistory) {
            try {
                firestore.collection("counters").document(entity.counterId)
                    .collection("history").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                counterHistoryDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                counterHistoryDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
