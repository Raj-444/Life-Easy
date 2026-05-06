package com.example.lifeeasy.domain.model

data class Event(
    val id: String = "",
    val title: String = "",
    val subjectId: String? = null,
    val eventType: String = "other",
    val description: String = "",
    val startTime: Long = 0L,
    val endTime: Long? = null,
    val location: String = "",
    val reminderMinutes: Int = 15,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
