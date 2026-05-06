package com.example.lifeeasy.data.local.dao

import androidx.room.*
import com.example.lifeeasy.data.local.entity.NoteEntity
import com.example.lifeeasy.data.local.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE syncStatus = :status")
    suspend fun getUnsyncedNotes(status: SyncStatus = SyncStatus.PENDING): List<NoteEntity>

    @Query("UPDATE notes SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
