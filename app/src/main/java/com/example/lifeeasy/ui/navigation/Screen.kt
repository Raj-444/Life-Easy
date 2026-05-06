package com.example.lifeeasy.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main
    object Home : Screen("home")
    object TaskList : Screen("task_list")
    object Counter : Screen("counter")
    object CounterDetail : Screen("counter_detail/{counterId}") {
        fun createRoute(counterId: String) = "counter_detail/$counterId"
    }
    object Attendance : Screen("attendance")
    object Debt : Screen("debt")
    object Event : Screen("event")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Pomodoro : Screen("pomodoro")
    object Notes : Screen("notes")
    object Health : Screen("health")
    object Clock : Screen("clock")
    object About : Screen("about")
    object Roadmap : Screen("roadmap")
}

