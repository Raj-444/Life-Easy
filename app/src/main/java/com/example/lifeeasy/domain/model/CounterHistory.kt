package com.example.lifeeasy.domain.model

data class CounterHistory(
    val id: String = "",
    val counterId: String = "",
    val oldCount: Int = 0,
    val newCount: Int = 0,
    val changeType: String = "",
    val timestamp: Long = 0L
)
