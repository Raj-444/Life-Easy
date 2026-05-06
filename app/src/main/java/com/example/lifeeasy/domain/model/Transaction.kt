package com.example.lifeeasy.domain.model

data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val category: String = "",
    val note: String = "",
    val date: Long = 0L,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
