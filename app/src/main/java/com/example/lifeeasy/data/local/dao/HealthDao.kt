package com.example.lifeeasy.data.local.dao

import androidx.room.*
import com.example.lifeeasy.data.local.entity.HealthEntity
import com.example.lifeeasy.data.local.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Query("SELECT * FROM health_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<HealthEntity>>

    @Query("SELECT * FROM health_logs WHERE type = :type AND date >= :startOfDay ORDER BY date DESC")
    fun getTodayLogsByType(type: String, startOfDay: Long): Flow<List<HealthEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HealthEntity)

    @Delete
    suspend fun delete(log: HealthEntity)

    @Query("SELECT * FROM health_logs WHERE syncStatus = :status")
    suspend fun getUnsyncedLogs(status: SyncStatus = SyncStatus.PENDING): List<HealthEntity>

    @Query("UPDATE health_logs SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
