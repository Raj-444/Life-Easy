package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roadmap_items")
data class RoadmapItemEntity(
    @PrimaryKey val id: String,
    val goalId: String, // foreign key to RoadmapEntity
    val title: String,
    val targetDate: Long? = null,
    val isCompleted: Boolean = false,
    val topic: String = ""
)
