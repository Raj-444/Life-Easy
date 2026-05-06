package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun syncWithCloud()
}
