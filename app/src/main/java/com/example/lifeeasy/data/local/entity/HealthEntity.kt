package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeeasy.data.local.SyncStatus

@Entity(tableName = "health_logs")
data class HealthEntity(
    @PrimaryKey val id: String,
    val type: String,
    val value: Float,
    val unit: String,
    val note: String,
    val date: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
