package com.example.lifeeasy.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lifeeasy.ui.components.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.R
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Password strength logic
    val passwordStrength = remember(password) {
        when {
            password.isEmpty() -> 0f
            password.length < 6 -> 0.3f
            password.any { it.isDigit() } && password.any { it.isUpperCase() } -> 1f
            else -> 0.6f
        }
    }
    val strengthColor by animateColorAsState(
        targetValue = when {
            passwordStrength <= 0.3f -> Color(0xFFFF6584) // Weak
            passwordStrength <= 0.6f -> Color(0xFFFFD700) // Medium
            else -> Color(0xFF00C853) // Strong
        }
    )

    // Navigate on success
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onRegisterSuccess()
    }

    // Show errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "LifeEasy Logo",
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Fill in the details to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(40.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Name Field
                        AuthTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Full Name",
                            icon = Icons.Default.Person,
                            imeAction = ImeAction.Next,
                            onAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Field
                        AuthTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            icon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            onAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        AuthTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible },
                            imeAction = ImeAction.Next,
                            onAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        // Strength Indicator
                        if (password.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                LinearProgressIndicator(
                                    progress = { passwordStrength },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp)),
                                    color = strengthColor,
                                    trackColor = Color.Transparent
                                )
                                Text(
                                    text = when {
                                        passwordStrength <= 0.3f -> "Weak"
                                        passwordStrength <= 0.6f -> "Medium"
                                        else -> "Strong"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = strengthColor,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm Password
                        AuthTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible },
                            imeAction = ImeAction.Done,
                            onAction = {
                                focusManager.clearFocus()
                                if (password == confirmPassword) {
                                    viewModel.registerWithEmail(email, password, name)
                                }
                            }
                        )

                        if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                            Text(
                                text = "Passwords do not match",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF6584),
                                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Register Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.registerWithEmail(email, password, name)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C63FF)
                            ),
                            enabled = !uiState.isLoading && password.isNotEmpty() && password == confirmPassword
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account? ", color = Color.White.copy(alpha = 0.6f))
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Sign In", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

