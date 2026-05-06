package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.SubjectDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.Subject
import com.example.lifeeasy.domain.repository.SubjectRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val firestore: FirebaseFirestore
) : SubjectRepository {

    override fun getAllSubjects(): Flow<List<Subject>> =
        subjectDao.getAllSubjects().map { list -> list.map { it.toDomain() } }

    override suspend fun getSubjectById(id: String): Subject? =
        subjectDao.getSubjectById(id)?.toDomain()

    override suspend fun saveSubject(subject: Subject) {
        subjectDao.insert(subject.toEntity(SyncStatus.PENDING))
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectDao.update(subject.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteSubject(subject: Subject) {
        subjectDao.delete(subject.toEntity())
    }

    override suspend fun syncPendingSubjects() {
        val unsynced = subjectDao.getUnsyncedSubjects()
        for (entity in unsynced) {
            try {
                firestore.collection("subjects").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                subjectDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                subjectDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
