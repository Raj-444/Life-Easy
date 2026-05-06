package com.example.lifeeasy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roadmap_goals")
data class RoadmapEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
