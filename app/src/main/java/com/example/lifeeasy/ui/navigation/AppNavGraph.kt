package com.example.lifeeasy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lifeeasy.ui.screens.auth.AuthViewModel
import com.example.lifeeasy.ui.screens.auth.LoginScreen
import com.example.lifeeasy.ui.screens.auth.RegisterScreen

/**
 * Builds the full navigation graph.
 *
 * @param startDestination either [Screen.Login] or [Screen.Home] depending on session state.
 * @param mainContent composable for the main app scaffold (contains its own inner NavHost for tabs).
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    mainContent: @Composable () -> Unit
) {
    // Shared AuthViewModel scoped to the nav graph so Login ↔ Register share state
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Auth flow ────────────────────────────────
        composable(Screen.Onboarding.route) {
            com.example.lifeeasy.ui.screens.auth.OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Main app ─────────────────────────────────
        composable(Screen.Home.route) {
            mainContent()
        }
    }
}
