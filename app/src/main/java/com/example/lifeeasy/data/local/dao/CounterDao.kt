package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.entity.CounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {

    @Query("SELECT * FROM counters ORDER BY createdAt DESC")
    fun getAllCounters(): Flow<List<CounterEntity>>

    @Query("SELECT * FROM counters WHERE id = :id")
    suspend fun getCounterById(id: String): CounterEntity?

    @Query("SELECT * FROM counters WHERE syncStatus != :status")
    suspend fun getUnsyncedCounters(status: SyncStatus = SyncStatus.SYNCED): List<CounterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(counter: CounterEntity)

    @Update
    suspend fun update(counter: CounterEntity)

    @Delete
    suspend fun delete(counter: CounterEntity)

    @Query("UPDATE counters SET count = count + 1, updatedAt = :timestamp, syncStatus = :status WHERE id = :id")
    suspend fun increment(id: String, timestamp: Long = System.currentTimeMillis(), status: SyncStatus = SyncStatus.PENDING)

    @Query("UPDATE counters SET count = count - 1, updatedAt = :timestamp, syncStatus = :status WHERE id = :id")
    suspend fun decrement(id: String, timestamp: Long = System.currentTimeMillis(), status: SyncStatus = SyncStatus.PENDING)

    @Query("UPDATE counters SET syncStatus = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long = System.currentTimeMillis())
}
