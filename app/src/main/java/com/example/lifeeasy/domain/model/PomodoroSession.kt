package com.example.lifeeasy.domain.model

data class PomodoroSession(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val type: String, // "work", "short_break", "long_break"
    val completed: Boolean,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
