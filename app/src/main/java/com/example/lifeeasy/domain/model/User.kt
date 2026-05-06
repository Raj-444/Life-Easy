package com.example.lifeeasy.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val lastSync: Long = 0L
)
