package com.example.lifeeasy.domain.model

data class Counter(
    val id: String = "",
    val label: String = "",
    val count: Int = 0,
    val targetCount: Int? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
