package com.example.lifeeasy.domain.model

data class GpaCourse(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val credits: Double = 3.0,
    val gradePoint: Double = 4.0
)

data class GpaResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val semesterName: String,
    val gpa: Double,
    val totalCredits: Double,
    val timestamp: Long = System.currentTimeMillis()
)
