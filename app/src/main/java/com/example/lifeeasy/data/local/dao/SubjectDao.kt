package com.example.lifeeasy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: String): SubjectEntity?

    @Query("SELECT * FROM subjects WHERE syncStatus != :status")
    suspend fun getUnsyncedSubjects(status: SyncStatus = SyncStatus.SYNCED): List<SubjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: SubjectEntity)

    @Update
    suspend fun update(subject: SubjectEntity)

    @Delete
    suspend fun delete(subject: SubjectEntity)

    @Query("UPDATE subjects SET syncStatus = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long = System.currentTimeMillis())
}
