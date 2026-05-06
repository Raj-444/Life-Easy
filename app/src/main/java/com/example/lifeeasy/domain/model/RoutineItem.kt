package com.example.lifeeasy.domain.model

data class RoutineItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val dayOfWeek: Int, // 1 = Sunday, 7 = Saturday (following Calendar.SUNDAY etc)
    val subjectName: String,
    val startTime: String, // "09:00 AM"
    val endTime: String,
    val room: String = "",
    val teacher: String = ""
)
