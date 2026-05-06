package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startTime >= :from AND startTime <= :to ORDER BY startTime ASC")
    fun getEventsBetween(from: Long, to: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE syncStatus != :status")
    suspend fun getUnsyncedEvents(status: SyncStatus = SyncStatus.SYNCED): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("UPDATE events SET syncStatus = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long = System.currentTimeMillis())
}
