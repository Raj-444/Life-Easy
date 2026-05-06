package com.example.lifeeasy.domain.model

data class Attendance(
    val id: String = "",
    val subjectId: String = "",
    val date: Long = 0L,
    val status: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
