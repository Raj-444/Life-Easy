package com.example.lifeeasy.domain.repository

import com.example.lifeeasy.domain.model.AuthResult
import com.example.lifeeasy.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Authentication contract — the domain layer has no knowledge of Firebase.
 */
interface AuthRepository {
    val currentUser: Flow<User?>
    val isLoggedIn: Boolean

    suspend fun loginWithEmail(email: String, password: String): AuthResult
    suspend fun registerWithEmail(email: String, password: String, name: String): AuthResult
    suspend fun loginWithGoogleIdToken(idToken: String): AuthResult
    suspend fun updateUserName(name: String): AuthResult
    suspend fun logout()
}
