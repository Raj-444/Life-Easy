package com.example.lifeeasy.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeeasy.domain.model.AuthResult
import com.example.lifeeasy.domain.model.User
import com.example.lifeeasy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the authentication screens.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** Reactive session — emits the current user or null. */
    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** True if Firebase has a persisted session (checked synchronously). */
    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    // ── Email / Password ──────────────────────────────

    fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email and password are required")
            return
        }
        performAuth { authRepository.loginWithEmail(email, password) }
    }

    fun registerWithEmail(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "All fields are required")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters")
            return
        }
        performAuth { authRepository.registerWithEmail(email, password, name) }
    }

    // ── Google ────────────────────────────────────────

    fun loginWithGoogleIdToken(idToken: String) {
        performAuth { authRepository.loginWithGoogleIdToken(idToken) }
    }

    // ── Profile ───────────────────────────────────────

    fun updateUserName(name: String) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Name cannot be empty")
            return
        }
        performAuth { authRepository.updateUserName(name) }
    }

    // ── Logout ────────────────────────────────────────

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState(isAuthenticated = false)
        }
    }

    // ── Helpers ───────────────────────────────────────

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun performAuth(block: suspend () -> AuthResult) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = block()) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState(isAuthenticated = true)
                }
                is AuthResult.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
                is AuthResult.Loading -> { /* no-op */ }
            }
        }
    }
}
