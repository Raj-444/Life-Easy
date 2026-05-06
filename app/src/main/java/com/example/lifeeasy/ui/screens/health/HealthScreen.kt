package com.example.lifeeasy.ui.screens.health

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.domain.model.HealthLog
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(viewModel: HealthViewModel, onNavigateBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var showWorkoutDialog by remember { mutableStateOf(false) }

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Health Tracker", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Hydration Card
                item {
                    HydrationCard(
                        todayWater = uiState.todayWater,
                        goal = uiState.waterGoal,
                        onAddWater = { viewModel.addWater(it) }
                    )
                }

                // 2. Workout Card
                item {
                    WorkoutCard(
                        todayWorkout = uiState.todayWorkout,
                        goal = uiState.workoutGoal,
                        onAddClick = { showWorkoutDialog = true }
                    )
                }

                // 3. Recent History
                item {
                    Text(
                        "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.recentLogs) { log ->
                    HealthLogItem(log = log, onDelete = { viewModel.deleteLog(log) })
                }
            }
        }

        if (showWorkoutDialog) {
            AddWorkoutDialog(
                onDismiss = { showWorkoutDialog = false },
                onSave = { duration, note ->
                    viewModel.addWorkout(duration, note)
                    showWorkoutDialog = false
                }
            )
        }
    }
}

@Composable
fun HydrationCard(todayWater: Float, goal: Float, onAddWater: (Float) -> Unit) {
    val progress = (todayWater / goal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress)

    GlassCard(modifier = Modifier.fillMaxWidth(), blur = 20.dp) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hydration", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${todayWater.toInt()} / ${goal.toInt()} ml", color = Color.White.copy(alpha = 0.7f))
                }
                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF2196F3),
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WaterButton("+250ml", 250f, onAddWater)
                WaterButton("+500ml", 500f, onAddWater)
                WaterButton("+1L", 1000f, onAddWater)
            }
        }
    }
}

@Composable
fun WaterButton(label: String, amount: Float, onClick: (Float) -> Unit) {
    OutlinedButton(
        onClick = { onClick(amount) },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Text(label)
    }
}

@Composable
fun WorkoutCard(todayWorkout: Float, goal: Float, onAddClick: () -> Unit) {
    val progress = (todayWorkout / goal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress)

    GlassCard(modifier = Modifier.fillMaxWidth(), blur = 20.dp) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Workout", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${todayWorkout.toInt()} / ${goal.toInt()} mins", color = Color.White.copy(alpha = 0.7f))
                }
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Workout")
            }
        }
    }
}

@Composable
fun HealthLogItem(log: HealthLog, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault())
    val color = if (log.type == "hydration") Color(0xFF2196F3) else Color(0xFF4CAF50)
    val icon = if (log.type == "hydration") Icons.Default.WaterDrop else Icons.Default.FitnessCenter

    GlassCard(modifier = Modifier.fillMaxWidth(), blur = 10.dp) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (log.type == "hydration") "Water Intake" else "Workout Session",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${log.value.toInt()}${log.unit} • ${sdf.format(Date(log.date))}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                if (log.note.isNotEmpty()) {
                    Text(log.note, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun AddWorkoutDialog(onDismiss: () -> Unit, onSave: (Float, String) -> Unit) {
    var duration by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Workout") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(duration.toFloatOrNull() ?: 0f, note) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
