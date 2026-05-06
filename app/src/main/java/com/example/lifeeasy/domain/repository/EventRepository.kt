package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsBetween(from: Long, to: Long): Flow<List<Event>>
    suspend fun getEventById(id: String): Event?
    suspend fun saveEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    suspend fun syncPendingEvents()
}
