package com.example.lifeeasy.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.lifeeasy.R
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.spacing
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.rounded.Warning

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userName: String = "User",
    onNavigateToTask: () -> Unit = {},
    onNavigateToCounter: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToDebt: () -> Unit = {},
    onNavigateToEvent: () -> Unit = {},
    onNavigateToPomodoro: () -> Unit = {},
    onNavigateToNotes: () -> Unit = {},
    onNavigateToHealth: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToClock: () -> Unit = {},
    onNavigateToRoadmap: () -> Unit = {},
    onNavigateToGpa: () -> Unit = {},
    onNavigateToRoutine: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = MaterialTheme.spacing

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                HomeTopBar(
                    uiState = uiState,
                    userName = userName,
                    onProfileClick = onNavigateToProfile,
                    onSettingsClick = onNavigateToSettings
                )
            },
            floatingActionButton = {
                SpeedDialFAB(
                    onAddTask = onNavigateToTask,
                    onAttendance = onNavigateToAttendance,
                    onLogDebt = onNavigateToDebt
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                contentPadding = PaddingValues(bottom = spacing.extraLarge * 2)
            ) {
                item {
                    Spacer(modifier = Modifier.height(spacing.small))
                    WelcomeHeader(userName)
                }

                item {
                    SummarySection(
                        uiState = uiState,
                        onTaskClick = onNavigateToTask,
                        onDebtClick = onNavigateToDebt,
                        onAttendanceClick = onNavigateToAttendance
                    )
                }

                item {
                    ProductivityScoreCard(score = uiState.productivityScore)
                }

                item {
                    LifeProgressSection(progress = uiState.lifeProgress)
                }

                item {
                    DailyQuoteSection()
                }

                if (uiState.lowestAttendance < 75.0) {
                    item {
                        AttendanceWarningCard(
                            subjectName = uiState.lowestAttendanceSubject,
                            percentage = uiState.lowestAttendance.toInt(),
                            onClick = onNavigateToAttendance
                        )
                    }
                }

                item {
                    FocusOverviewCard(
                        data = uiState.focusData,
                        onClick = onNavigateToPomodoro
                    )
                }

                item {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = spacing.small)
                    )
                }

                item {
                    QuickActionGrid(
                        onAddTask = onNavigateToTask,
                        onNewEvent = onNavigateToEvent,
                        onLogDebt = onNavigateToDebt
                    )
                }

                item {
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = spacing.small)
                    )
                }

                item {
                    TimeProgressCard()
                }

                item {
                    Spacer(modifier = Modifier.height(spacing.medium))
                }

                item {
                    FeatureGrid(
                        onNavigateToTask = onNavigateToTask,
                        onNavigateToCounter = onNavigateToCounter,
                        onNavigateToAttendance = onNavigateToAttendance,
                        onNavigateToDebt = onNavigateToDebt,
                        onNavigateToEvent = onNavigateToEvent,
                        onNavigateToPomodoro = onNavigateToPomodoro,
                        onNavigateToNotes = onNavigateToNotes,
                        onNavigateToHealth = onNavigateToHealth,
                        onNavigateToClock = onNavigateToClock,
                        onNavigateToRoadmap = onNavigateToRoadmap,
                        onNavigateToGpa = onNavigateToGpa,
                        onNavigateToRoutine = onNavigateToRoutine
                    )
                }
            }
        }
    }
}

@Composable
fun TimeProgressCard() {
    val spacing = MaterialTheme.spacing
    val cal = java.util.Calendar.getInstance()
    val dayOfYear = cal.get(java.util.Calendar.DAY_OF_YEAR)
    val maxDays = cal.getActualMaximum(java.util.Calendar.DAY_OF_YEAR)
    val daysRemaining = maxDays - dayOfYear
    val yearProgress = (dayOfYear.toFloat() / maxDays.toFloat()) * 100f
    
    val weekOfYear = cal.get(java.util.Calendar.WEEK_OF_YEAR)
    val maxWeeks = cal.getActualMaximum(java.util.Calendar.WEEK_OF_YEAR)
    val weeksRemaining = maxWeeks - weekOfYear
    
    val month = cal.get(java.util.Calendar.MONTH) + 1
    val monthsRemaining = 12 - month

    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = spacing.small)) {
        Column {
            Text(
                "Time Remaining in ${cal.get(java.util.Calendar.YEAR)}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(spacing.medium))
            
            // Progress Bar
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { yearProgress / 100f },
                    modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Primary,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text("${yearProgress.toInt()}%", color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.height(spacing.medium))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TimeStat("Days", daysRemaining.toString())
                TimeStat("Weeks", weeksRemaining.toString())
                TimeStat("Months", monthsRemaining.toString())
            }
        }
    }
}

@Composable
fun TimeStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = Primary, fontWeight = FontWeight.Black)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    uiState: HomeUiState,
    userName: String,
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var showNotifications by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("LifeEasy", fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        },
        actions = {
            Box {
                IconButton(onClick = { showNotifications = true }) {
                    BadgedBox(
                        badge = { Badge(containerColor = Color.Red) { Text("!", color = Color.White) } }
                    ) {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                }
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Rounded.Person, contentDescription = "Profile", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )

    if (showNotifications) {
        NotificationCenterSheet(
            uiState = uiState, // We need to pass uiState here
            onDismiss = { showNotifications = false }
        )
    }
}

@Composable
fun WelcomeHeader(userName: String) {
    val spacing = MaterialTheme.spacing
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Primary, Accent)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(spacing.large)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "Hello,",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = userName,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterEnd)
                .graphicsLayer { alpha = 0.2f },
            tint = Color.White
        )
    }
}


@Composable
fun SummarySection(
    uiState: HomeUiState,
    onTaskClick: () -> Unit = {},
    onDebtClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Tasks",
                value = "${uiState.pendingTasksCount}",
                subtitle = "Pending",
                icon = Icons.AutoMirrored.Filled.List,
                containerColor = Primary.copy(alpha = 0.15f),
                onClick = onTaskClick
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Debt",
                value = "৳${uiState.totalDebtBalance.toInt()}",
                subtitle = if (uiState.totalDebtBalance >= 0) "To Receive" else "To Pay",
                icon = Icons.Default.AccountBalanceWallet,
                containerColor = Accent.copy(alpha = 0.15f),
                onClick = onDebtClick
            )
        }
        
        AttendanceSummaryCard(
            isPresent = uiState.isPresentToday,
            onClick = onAttendanceClick
        )
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit = {}
) {
    val spacing = MaterialTheme.spacing
    GlassCard(
        modifier = modifier.clickable { onClick() },
        containerColor = containerColor
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Accent
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AttendanceSummaryCard(isPresent: Boolean, onClick: () -> Unit = {}) {
    val spacing = MaterialTheme.spacing
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPresent) Icons.Rounded.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isPresent) Color(0xFF4CAF50) else Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(spacing.medium))
                Column {
                    Text(
                        text = "Attendance",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = if (isPresent) "You are marked present today" else "You haven't marked attendance yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceWarningCard(subjectName: String, percentage: Int, onClick: () -> Unit = {}) {
    val spacing = MaterialTheme.spacing
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        containerColor = Color(0xFFFF5252).copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFF5252).copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF5252)
                )
            }
            Spacer(modifier = Modifier.width(spacing.medium))
            Column {
                Text(
                    text = "Attendance Warning",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Your attendance in $subjectName is $percentage%. Please attend more classes!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun FocusOverviewCard(data: List<FocusData>, onClick: () -> Unit = {}) {
    val spacing = MaterialTheme.spacing
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Focus Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Last 7 Days",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = "24.5 hrs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(spacing.large))

            // Bar Chart using Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = spacing.small)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barWidth = size.width / (data.size * 2f)
                    val maxHours = 8f // Assume 8 hours max for scaling
                    
                    data.forEachIndexed { index, focusData ->
                        val barHeight = (focusData.hours / maxHours) * size.height
                        val x = (index * 2f + 0.5f) * barWidth
                        val y = size.height - barHeight
                        
                        // Draw Bar
                        drawRoundRect(
                            color = if (index == data.size - 1) Primary else Color.White.copy(alpha = 0.3f),
                            topLeft = androidx.compose.ui.geometry.Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                        )
                    }
                }
            }
            
            // Days labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                data.forEach {
                    Text(
                        text = it.day.take(1),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun SpeedDialFAB(
    onAddTask: () -> Unit = {},
    onAttendance: () -> Unit = {},
    onLogDebt: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(16.dp)
        ) {
            // Mini FABs
            AnimatedVisibility(
                visible = expanded,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    MiniFabItem(label = "New Task", icon = Icons.Default.Add, color = Color(0xFF6C63FF), onClick = { expanded = false; onAddTask() })
                    MiniFabItem(label = "Attendance", icon = Icons.Default.Check, color = Color(0xFF4CAF50), onClick = { expanded = false; onAttendance() })
                    MiniFabItem(label = "Log Debt", icon = Icons.Default.AccountBalanceWallet, color = Color(0xFFFF6584), onClick = { expanded = false; onLogDebt() })
                }
            }
            
            // Main FAB
            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.graphicsLayer {
                    rotationZ = if (expanded) 45f else 0f
                }
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun MiniFabItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = color,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun FeatureGrid(
    onNavigateToTask: () -> Unit,
    onNavigateToCounter: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToDebt: () -> Unit,
    onNavigateToEvent: () -> Unit,
    onNavigateToPomodoro: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToHealth: () -> Unit,
    onNavigateToClock: () -> Unit,
    onNavigateToRoadmap: () -> Unit,
    onNavigateToGpa: () -> Unit,
    onNavigateToRoutine: () -> Unit
) {
    val features = listOf(
        FeatureItem("Tasks", Icons.AutoMirrored.Filled.List, Color(0xFF6C63FF), onNavigateToTask),
        FeatureItem("Counter", Icons.Default.PinDrop, Color(0xFF00BCD4), onNavigateToCounter),
        FeatureItem("Attendance", Icons.Default.CalendarMonth, Color(0xFF4CAF50), onNavigateToAttendance),
        FeatureItem("Debt", Icons.Default.AccountBalanceWallet, Color(0xFFFF6584), onNavigateToDebt),
        FeatureItem("Events", Icons.Default.Event, Color(0xFFFF9800), onNavigateToEvent),
        FeatureItem("Pomodoro", Icons.Default.Timer, Color(0xFFE91E63), onNavigateToPomodoro),
        FeatureItem("Notes", Icons.Default.NoteAlt, Color(0xFF9C27B0), onNavigateToNotes),
        FeatureItem("Health", Icons.Default.FavoriteBorder, Color(0xFFF44336), onNavigateToHealth),
        FeatureItem("Clock", Icons.Default.AccessTime, Color(0xFF607D8B), onNavigateToClock),
        FeatureItem("Roadmap", Icons.Default.Map, Color(0xFFFFC107), onNavigateToRoadmap),
        FeatureItem("GPA", Icons.Default.Calculate, Color(0xFF3F51B5), onNavigateToGpa),
        FeatureItem("Routine", Icons.Default.ViewAgenda, Color(0xFF9E9E9E), onNavigateToRoutine)
    )

    val spacing = MaterialTheme.spacing

    Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
        features.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                row.forEach { feature ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { feature.onClick() },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlassCard(
                            modifier = Modifier.size(64.dp),
                            cornerRadius = 18.dp
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = feature.icon,
                                    contentDescription = feature.label,
                                    tint = feature.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = feature.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                // Fill remaining if row not complete
                repeat(4 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

data class FeatureItem(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun QuickActionGrid(
    onAddTask: () -> Unit = {},
    onNewEvent: () -> Unit = {},
    onLogDebt: () -> Unit = {}
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        QuickActionButton(
            modifier = Modifier.weight(1f),
            label = "Add Task",
            icon = Icons.Default.Add,
            color = Color(0xFF6C63FF),
            onClick = onAddTask
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            label = "New Event",
            icon = Icons.Default.Event,
            color = Color(0xFFFF6584),
            onClick = onNewEvent
        )
        QuickActionButton(
            modifier = Modifier.weight(1f),
            label = "Log Debt",
            icon = Icons.Default.AccountBalanceWallet,
            color = Color(0xFF4CAF50),
            onClick = onLogDebt
        )
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit = {}
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassCard(
            modifier = Modifier.size(64.dp),
            cornerRadius = 16.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = label, 
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun LifeProgressSection(progress: LifeProgress) {
    val spacing = MaterialTheme.spacing
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Life Progress",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(spacing.small))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProgressMetric(label = "Day", progress = progress.day, color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(12.dp))
                ProgressMetric(label = "Week", progress = progress.week, color = Color(0xFF2196F3))
                Spacer(modifier = Modifier.height(12.dp))
                ProgressMetric(label = "Month", progress = progress.month, color = Color(0xFFFF9800))
                Spacer(modifier = Modifier.height(12.dp))
                ProgressMetric(label = "Year", progress = progress.year, color = Color(0xFFE91E63))
            }
        }
    }
}

@Composable
fun ProgressMetric(label: String, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun DailyQuoteSection() {
    val quotes = listOf(
        "The best way to predict the future is to create it.",
        "Success is not final, failure is not fatal: it is the courage to continue that counts.",
        "Your time is limited, so don't waste it living someone else's life.",
        "The only way to do great work is to love what you do.",
        "Don't watch the clock; do what it does. Keep going."
    )
    val quote = remember { quotes.random() }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.FormatQuote,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Today's Inspiration",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ProductivityScoreCard(score: Int) {
    val spacing = MaterialTheme.spacing
    val level = when {
        score >= 80 -> "Elite"
        score >= 60 -> "Productive"
        score >= 40 -> "Steady"
        else -> "Starting"
    }
    
    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = spacing.small)) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                CircularProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = Primary,
                    trackColor = Color.White.copy(alpha = 0.1f),
                    strokeWidth = 8.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = "Daily Productivity",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Level: $level",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Complete tasks and focus sessions to increase your score!",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
