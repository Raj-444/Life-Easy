package com.example.lifeeasy.domain.model

data class Expense(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String, // "food", "travel", "study", etc.
    val type: String, // "expense" or "income"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
