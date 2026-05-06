package com.example.lifeeasy.domain.model

data class Subject(
    val id: String = "",
    val name: String = "",
    val teacherName: String = "",
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
