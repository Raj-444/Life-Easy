package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.data.local.SyncStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Int = 0,            // 0=Low, 1=Medium, 2=High
    val dueDate: Long? = null,
    val reminderTime: Long? = null,   // epoch millis for alarm
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
