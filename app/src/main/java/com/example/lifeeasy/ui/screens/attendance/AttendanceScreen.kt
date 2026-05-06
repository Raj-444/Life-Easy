package com.example.lifeeasy.ui.screens.attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import com.example.lifeeasy.domain.model.Subject
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.spacing
import com.example.lifeeasy.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: AttendanceViewModel, onNavigateBack: () -> Unit) {
    val subjects by viewModel.subjects.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val spacing = MaterialTheme.spacing

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Attendance Tracker", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddSubjectDialog() },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.LibraryAdd, contentDescription = "Add Subject")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (subjects.isEmpty()) {
                    EmptyAttendanceState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                        contentPadding = PaddingValues(horizontal = spacing.medium, vertical = spacing.medium)
                    ) {
                        items(subjects, key = { it.id }) { subject ->
                            SubjectAttendanceCard(
                                subject = subject,
                                onMarkPresent = { viewModel.markAttendance(subject.id, "present") },
                                onMarkAbsent = { viewModel.markAttendance(subject.id, "absent") },
                                onDelete = { viewModel.deleteSubject(subject) }
                            )
                        }
                    }
                }
            }

            if (uiState.showAddSubjectDialog) {
                AddSubjectDialog(
                    onDismiss = { viewModel.dismissAddSubjectDialog() },
                    onAdd = { name, teacher -> viewModel.addSubject(name, teacher) }
                )
            }
        }
    }
}

@Composable
fun SubjectAttendanceCard(
    subject: Subject,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val attended = subject.attendedClasses
    val total = subject.totalClasses
    val percentage = if (total > 0) (attended.toFloat() / total.toFloat() * 100) else 0f
    
    val animatedPercentage by animateFloatAsState(targetValue = percentage / 100f)
    val statusColor = if (percentage >= 75) Color(0xFF4CAF50) else Color(0xFFFF5252)

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        subject.name.take(1).uppercase(),
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(subject.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    if (subject.teacherName.isNotBlank()) {
                        Text(subject.teacherName, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.3f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress and Stats
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { animatedPercentage },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 6.dp,
                        color = statusColor,
                        trackColor = Color.White.copy(alpha = 0.1f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(
                        "${percentage.toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatLabel("Attended", attended.toString(), Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        StatLabel("Total", total.toString(), Color.White.copy(alpha = 0.6f))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Safe Zone Calculation
                    SafeZoneBadge(attended, total)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onMarkPresent,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Present", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onMarkAbsent,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Absent", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatLabel(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun SafeZoneBadge(attended: Int, total: Int) {
    if (total == 0) return

    val currentPercentage = (attended.toFloat() / total.toFloat() * 100)
    
    if (currentPercentage >= 75f) {
        val canMiss = floor((attended / 0.75) - total).toInt()
        Surface(
            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = if (canMiss > 0) "Safe: Can miss next $canMiss classes" else "Safe: Don't miss next class",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF81C784)
            )
        }
    } else {
        val mustAttend = ceil((0.75 * total - attended) / 0.25).toInt()
        Surface(
            color = Color(0xFFFF5252).copy(alpha = 0.1f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = "Critical: Attend next $mustAttend classes",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFE57373)
            )
        }
    }
}

@Composable
fun EmptyAttendanceState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎓", style = androidx.compose.ui.text.TextStyle(fontSize = 60.sp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("No subjects found", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text("Start tracking by adding a subject", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun AddSubjectDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Subject") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") })
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher (Optional)") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(name, teacher) }, enabled = name.isNotBlank()) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
