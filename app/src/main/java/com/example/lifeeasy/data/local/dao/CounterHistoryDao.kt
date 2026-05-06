package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.entity.CounterHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterHistoryDao {

    @Query("SELECT * FROM counter_history WHERE counterId = :counterId ORDER BY timestamp DESC")
    fun getHistoryForCounter(counterId: String): Flow<List<CounterHistoryEntity>>

    @Query("SELECT * FROM counter_history WHERE syncStatus != :status")
    suspend fun getUnsyncedHistory(status: SyncStatus = SyncStatus.SYNCED): List<CounterHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: CounterHistoryEntity)

    @Query("UPDATE counter_history SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
