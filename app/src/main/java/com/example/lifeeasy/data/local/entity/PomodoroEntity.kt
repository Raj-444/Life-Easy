package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.data.local.SyncStatus

@Entity(tableName = "pomodoro_sessions")
data class PomodoroEntity(
    @PrimaryKey val id: String,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val type: String,
    val completed: Boolean,
    val date: Long,
    val createdAt: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
