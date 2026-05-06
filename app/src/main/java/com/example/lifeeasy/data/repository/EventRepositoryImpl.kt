package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.EventDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.Event
import com.example.lifeeasy.domain.repository.EventRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val firestore: FirebaseFirestore
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> =
        eventDao.getAllEvents().map { list -> list.map { it.toDomain() } }

    override fun getEventsBetween(from: Long, to: Long): Flow<List<Event>> =
        eventDao.getEventsBetween(from, to).map { list -> list.map { it.toDomain() } }

    override suspend fun getEventById(id: String): Event? =
        eventDao.getEventById(id)?.toDomain()

    override suspend fun saveEvent(event: Event) {
        eventDao.insert(event.toEntity(SyncStatus.PENDING))
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.update(event.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteEvent(event: Event) {
        eventDao.delete(event.toEntity())
    }

    override suspend fun syncPendingEvents() {
        val unsynced = eventDao.getUnsyncedEvents()
        for (entity in unsynced) {
            try {
                firestore.collection("events").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                eventDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                eventDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
