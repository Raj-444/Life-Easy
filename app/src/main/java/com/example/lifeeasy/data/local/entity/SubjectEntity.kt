package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.data.local.SyncStatus

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val teacherName: String = "",
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
