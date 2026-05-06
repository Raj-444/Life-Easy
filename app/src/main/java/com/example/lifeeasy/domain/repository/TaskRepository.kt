package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getPendingTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: String): Task?
    suspend fun saveTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun syncPendingTasks()
}
