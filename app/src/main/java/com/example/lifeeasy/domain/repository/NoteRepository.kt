package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun syncPendingNotes()
}
