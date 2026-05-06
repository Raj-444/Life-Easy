package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.data.local.SyncStatus

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subjectId: String? = null,
    val eventType: String = "other", // exam, presentation, lab, other
    val description: String = "",
    val startTime: Long,
    val endTime: Long? = null,
    val location: String = "",
    val reminderMinutes: Int = 15,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
