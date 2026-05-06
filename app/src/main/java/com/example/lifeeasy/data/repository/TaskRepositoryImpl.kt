package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.SyncStatus
import com.example.lifeeasy.data.local.dao.TaskDao
import com.example.lifeeasy.data.local.entity.toDomain
import com.example.lifeeasy.data.local.entity.toEntity
import com.example.lifeeasy.domain.model.Task
import com.example.lifeeasy.domain.repository.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }

    override fun getPendingTasks(): Flow<List<Task>> =
        taskDao.getPendingTasks().map { list -> list.map { it.toDomain() } }

    override suspend fun getTaskById(id: String): Task? =
        taskDao.getTaskById(id)?.toDomain()

    override suspend fun saveTask(task: Task) {
        taskDao.insert(task.toEntity(SyncStatus.PENDING))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity(SyncStatus.PENDING))
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun syncPendingTasks() {
        val unsynced = taskDao.getUnsyncedTasks()
        for (entity in unsynced) {
            try {
                firestore.collection("tasks").document(entity.id)
                    .set(entity.toDomain())
                    .await()
                taskDao.updateSyncStatus(entity.id, SyncStatus.SYNCED)
            } catch (e: Exception) {
                taskDao.updateSyncStatus(entity.id, SyncStatus.FAILED)
            }
        }
    }
}
