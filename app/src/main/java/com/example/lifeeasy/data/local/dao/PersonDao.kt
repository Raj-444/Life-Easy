package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun getAllPersons(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPersonById(id: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE syncStatus != :status")
    suspend fun getUnsyncedPersons(status: SyncStatus = SyncStatus.SYNCED): List<PersonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: PersonEntity)

    @Update
    suspend fun update(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("UPDATE persons SET syncStatus = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long = System.currentTimeMillis())
}
