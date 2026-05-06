package com.example.lifeeasy.ui.screens.pomodoro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel, onNavigateBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = MaterialTheme.spacing

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Focus Timer", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleSettings(true) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Session Type Selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(4.dp)
                ) {
                    SessionChip("Work", uiState.sessionType == "work") { viewModel.setSessionType("work") }
                    SessionChip("Short Break", uiState.sessionType == "short_break") { viewModel.setSessionType("short_break") }
                    SessionChip("Long Break", uiState.sessionType == "long_break") { viewModel.setSessionType("long_break") }
                }

                Spacer(modifier = Modifier.height(60.dp))

                // 2. Timer Circle
                TimerCircle(
                    timeLeft = uiState.timeLeft,
                    totalTime = uiState.totalTime,
                    sessionType = uiState.sessionType
                )

                Spacer(modifier = Modifier.height(60.dp))

                // 3. Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                    }

                    FloatingActionButton(
                        onClick = {
                            if (uiState.isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                        },
                        containerColor = Primary,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Start/Pause",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.setSessionType(if (uiState.sessionType == "work") "short_break" else "work") },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Skip", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 4. Stats
                GlassCard(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    blur = 20.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Completed focus sessions today: ${uiState.completedSessionsToday}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (uiState.showSettings) {
            PomodoroSettingsDialog(
                uiState = uiState,
                onDismiss = { viewModel.toggleSettings(false) },
                onSave = { w, s, l -> viewModel.updateSettings(w, s, l) }
            )
        }
    }
}

@Composable
fun SessionChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TimerCircle(timeLeft: Long, totalTime: Long, sessionType: String) {
    val progress = if (totalTime > 0) timeLeft.toFloat() / totalTime.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000)
    )
    
    val color = when (sessionType) {
        "work" -> Color(0xFFFF5252) // Bright Red for Work
        "short_break" -> Color(0xFF69F0AE) // Mint Green for Short Break
        else -> Color(0xFF40C4FF) // Light Blue for Long Break
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        // Massive Analog Clock Face
        Canvas(modifier = Modifier.size(340.dp)) {
            val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2
            
            // Outer Ring
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = radius,
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Ticks for minutes
            for (i in 0 until 60) {
                val angle = i * 6 * Math.PI / 180
                val tickLength = if (i % 5 == 0) 16.dp.toPx() else 8.dp.toPx()
                val strokeW = if (i % 5 == 0) 3.dp.toPx() else 1.dp.toPx()
                val start = androidx.compose.ui.geometry.Offset(
                    (center.x + (radius - tickLength) * Math.sin(angle)).toFloat(),
                    (center.y - (radius - tickLength) * Math.cos(angle)).toFloat()
                )
                val end = androidx.compose.ui.geometry.Offset(
                    (center.x + radius * Math.sin(angle)).toFloat(),
                    (center.y - radius * Math.cos(angle)).toFloat()
                )
                drawLine(
                    color = if (i % 5 == 0) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f),
                    start = start,
                    end = end,
                    strokeWidth = strokeW
                )
            }

            // Sweeping Progress Arc (Analog pie representation)
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            // Progress Edge Line
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
            
            // Analog Hand (Pointer)
            val handAngle = (animatedProgress * 360f - 90f) * Math.PI / 180
            val handEnd = androidx.compose.ui.geometry.Offset(
                (center.x + (radius - 20.dp.toPx()) * Math.cos(handAngle)).toFloat(),
                (center.y + (radius - 20.dp.toPx()) * Math.sin(handAngle)).toFloat()
            )
            drawLine(
                color = color,
                start = center,
                end = handEnd,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(color = color, radius = 6.dp.toPx(), center = center)
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = (-40).dp)) {
            val minutes = (timeLeft / 1000) / 60
            val seconds = (timeLeft / 1000) % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            )
            Text(
                text = sessionType.replace("_", " ").uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 4.sp
                ),
                color = color,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun PomodoroSettingsDialog(
    uiState: PomodoroUiState,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var work by remember { mutableStateOf(uiState.workDuration.toString()) }
    var short by remember { mutableStateOf(uiState.shortBreakDuration.toString()) }
    var long by remember { mutableStateOf(uiState.longBreakDuration.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Timer Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = work, onValueChange = { work = it }, label = { Text("Work Duration (min)") })
                OutlinedTextField(value = short, onValueChange = { short = it }, label = { Text("Short Break (min)") })
                OutlinedTextField(value = long, onValueChange = { long = it }, label = { Text("Long Break (min)") })
            }
        },
        confirmButton = {
            Button(onClick = { 
                onSave(work.toIntOrNull() ?: 25, short.toIntOrNull() ?: 5, long.toIntOrNull() ?: 15) 
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
