package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.data.local.SyncStatus

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val date: Long,                     // epoch millis for the day
    val status: String,                 // "present", "absent", "late"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
