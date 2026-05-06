package com.example.lifeeasy.domain.model

data class HealthLog(
    val id: String,
    val type: String, // "hydration", "workout"
    val value: Float,
    val unit: String, // "ml", "mins", "kcal"
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)
