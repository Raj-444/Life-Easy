package com.example.lifeeasy.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val color: Int, // Hex color or Int representation
    val isPinned: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
