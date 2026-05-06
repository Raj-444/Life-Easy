package com.example.lifeeasy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import com.example.lifeeasy.ui.navigation.Screen
import com.example.lifeeasy.ui.theme.LifeEasyTheme
import com.example.lifeeasy.ui.screens.auth.*
import com.example.lifeeasy.ui.screens.home.*
import com.example.lifeeasy.ui.screens.task.*
import com.example.lifeeasy.ui.screens.counter.*
import com.example.lifeeasy.ui.screens.attendance.*
import com.example.lifeeasy.ui.screens.debt.*
import com.example.lifeeasy.ui.screens.event.*
import com.example.lifeeasy.ui.screens.pomodoro.*
import com.example.lifeeasy.ui.screens.notes.*
import com.example.lifeeasy.ui.screens.health.*
import com.example.lifeeasy.ui.screens.profile.ProfileScreen
import com.example.lifeeasy.ui.screens.settings.SettingsScreen
import com.example.lifeeasy.ui.screens.about.AboutScreen
import com.example.lifeeasy.ui.screens.clock.ClockScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Permission launcher for notifications
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not — we don't block the user */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable edge-to-edge BEFORE super.onCreate()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        requestNotificationPermissionIfNeeded()

        setContent {
            LifeEasyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()

                    AppNavGraph(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // Determine start destination based on existing Firebase session
    val startDestination = remember {
        if (authViewModel.isLoggedIn) Screen.Home.route else Screen.Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val currentUser by authViewModel.currentUser.collectAsState()
            HomeScreen(
                viewModel = homeViewModel,
                userName = currentUser?.name?.split(" ")?.firstOrNull() ?: "User",
                onNavigateToTask = { navController.navigate(Screen.TaskList.route) },
                onNavigateToCounter = { navController.navigate(Screen.Counter.route) },
                onNavigateToAttendance = { navController.navigate(Screen.Attendance.route) },
                onNavigateToDebt = { navController.navigate(Screen.Debt.route) },
                onNavigateToEvent = { navController.navigate(Screen.Event.route) },
                onNavigateToPomodoro = { navController.navigate(Screen.Pomodoro.route) },
                onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                onNavigateToHealth = { navController.navigate(Screen.Health.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToClock = { navController.navigate(Screen.Clock.route) },
                onNavigateToRoadmap = { navController.navigate(Screen.Roadmap.route) },
                onNavigateToGpa = { navController.navigate(Screen.Gpa.route) },
                onNavigateToRoutine = { navController.navigate(Screen.Routine.route) }
            )
        }
        composable(Screen.TaskList.route) {
            val taskViewModel: TaskViewModel = hiltViewModel()
            TaskListScreen(
                viewModel = taskViewModel
            )
        }
        composable(Screen.Counter.route) {
            val counterViewModel: CounterViewModel = hiltViewModel()
            CounterScreen(
                viewModel = counterViewModel,
                onCounterClick = { id -> navController.navigate(Screen.CounterDetail.createRoute(id)) }
            )
        }
        composable(
            route = Screen.CounterDetail.route,
            arguments = listOf(navArgument("counterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val counterId = backStackEntry.arguments?.getString("counterId") ?: return@composable
            val counterViewModel: CounterViewModel = hiltViewModel()
            CounterDetailScreen(
                counterId = counterId,
                viewModel = counterViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Attendance.route) {
            val attendanceViewModel: AttendanceViewModel = hiltViewModel()
            AttendanceScreen(
                viewModel = attendanceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Debt.route) {
            val debtViewModel: DebtViewModel = hiltViewModel()
            DebtScreen(
                viewModel = debtViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Event.route) {
            val eventViewModel: EventViewModel = hiltViewModel()
            EventScreen(
                viewModel = eventViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Pomodoro.route) {
            val pomodoroViewModel: PomodoroViewModel = hiltViewModel()
            PomodoroScreen(
                viewModel = pomodoroViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Notes.route) {
            val notesViewModel: NotesViewModel = hiltViewModel()
            NotesScreen(
                viewModel = notesViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Clock.route) {
            ClockScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Health.route) {
            val healthViewModel: HealthViewModel = hiltViewModel()
            HealthScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Roadmap.route) {
            com.example.lifeeasy.ui.screens.roadmap.RoadmapScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Gpa.route) {
            com.example.lifeeasy.ui.screens.gpa.GpaCalculatorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Routine.route) {
            val routineViewModel: com.example.lifeeasy.ui.screens.routine.RoutineViewModel = hiltViewModel()
            com.example.lifeeasy.ui.screens.routine.RoutineScreen(
                viewModel = routineViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
