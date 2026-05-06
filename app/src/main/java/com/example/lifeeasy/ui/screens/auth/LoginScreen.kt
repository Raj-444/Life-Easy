package com.example.lifeeasy.ui.screens.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.lifeeasy.ui.components.*
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.ui.theme.spacing
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import com.example.lifeeasy.R
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthHandler = remember { GoogleAuthHandler(context) }

    // Navigate on success
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) onLoginSuccess()
    }

    // Show errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val spacing = MaterialTheme.spacing

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
                    .padding(spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(spacing.extraLarge * 2))

                // App Logo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "LifeEasy Logo",
                        modifier = Modifier.size(72.dp)
                    )
                }

                Spacer(modifier = Modifier.height(spacing.large))

                Text(
                    text = "LifeEasy",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Simplify your student life",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(spacing.extraLarge))

                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(spacing.medium))

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

                        Spacer(modifier = Modifier.height(spacing.small))

                        // Password Field
                        AuthTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible },
                            imeAction = ImeAction.Done,
                            onAction = {
                                focusManager.clearFocus()
                                viewModel.loginWithEmail(email, password)
                            }
                        )

                        Spacer(modifier = Modifier.height(spacing.large))

                        // Login Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.loginWithEmail(email, password)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            ),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.15f))
                    Text(
                        "  OR  ",
                        color = Color.White.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.labelSmall
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.15f))
                }

                Spacer(modifier = Modifier.height(spacing.large))

                // Google Sign-In
                var isGoogleLoading by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = {
                        if (!isGoogleLoading) {
                            isGoogleLoading = true
                            scope.launch {
                                val idToken = googleAuthHandler.signIn()
                                if (idToken != null) {
                                    viewModel.loginWithGoogleIdToken(idToken)
                                } else {
                                    snackbarHostState.showSnackbar("Google Sign-In failed. Please try again.")
                                }
                                isGoogleLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.2f)
                    ),
                    enabled = !isGoogleLoading
                ) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Signing in...", fontWeight = FontWeight.Medium)
                    } else {
                        Text("G", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Continue with Google", fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Don't have an account? ", color = Color.White.copy(alpha = 0.6f))
                    TextButton(onClick = onNavigateToRegister) {
                        Text("Sign Up", color = Accent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


