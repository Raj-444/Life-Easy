package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAllSubjects(): Flow<List<Subject>>
    suspend fun getSubjectById(id: String): Subject?
    suspend fun saveSubject(subject: Subject)
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(subject: Subject)
    suspend fun syncPendingSubjects()
}
