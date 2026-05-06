package com.example.lifeeasy.data.local.dao

import androidx.room.*
import com.example.lifeeasy.data.local.entity.PomodoroEntity
import com.example.lifeeasy.data.local.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<PomodoroEntity>>

    @Query("SELECT * FROM pomodoro_sessions WHERE date >= :startDate AND date <= :endDate")
    fun getSessionsForRange(startDate: Long, endDate: Long): Flow<List<PomodoroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PomodoroEntity)

    @Delete
    suspend fun delete(session: PomodoroEntity)

    @Query("SELECT * FROM pomodoro_sessions WHERE syncStatus = :status")
    suspend fun getUnsyncedSessions(status: SyncStatus = SyncStatus.PENDING): List<PomodoroEntity>

    @Query("UPDATE pomodoro_sessions SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
