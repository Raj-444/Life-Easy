package com.example.lifeeasy.domain.model

data class DebtTransaction(
    val id: String = "",
    val personId: String = "",
    val amount: Double = 0.0,
    val type: String = "", // "given" or "received"
    val note: String = "",
    val date: Long = 0L,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
