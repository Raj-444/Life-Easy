package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.NoteDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.Note
import com.example.lifeeasy.domain.repository.NoteRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val firestore: FirebaseFirestore
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { list -> list.map { it.toDomain() } }

    override suspend fun saveNote(note: Note) {
        noteDao.insert(note.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note.toEntity())
    }

    override suspend fun syncPendingNotes() {
        val unsyncedNotes = noteDao.getUnsyncedNotes()
        for (entity in unsyncedNotes) {
            try {
                firestore.collection("notes").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                noteDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                noteDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
