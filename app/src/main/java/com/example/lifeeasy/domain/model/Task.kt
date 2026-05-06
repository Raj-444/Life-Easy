package com.example.lifeeasy.domain.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Int = 0,           // 0=Low, 1=Medium, 2=High
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
