package com.example.lifeeasy.domain.model

/**
 * Sealed class representing the result of an authentication operation.
 */
sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}
