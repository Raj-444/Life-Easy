package com.example.lifeeasy.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterSheet(
    uiState: HomeUiState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        contentColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Notifications & Activity",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    NotificationHeader("Important")
                }

                if (uiState.lowestAttendance < 75.0) {
                    item {
                        NotificationItem(
                            title = "Low Attendance Warning!",
                            description = "Your attendance in ${uiState.lowestAttendanceSubject} is only ${uiState.lowestAttendance.toInt()}%. Try to attend more classes.",
                            icon = Icons.Default.Notifications,
                            color = Color(0xFFFF5252)
                        )
                    }
                }

                item {
                    NotificationItem(
                        title = "Welcome to LifeEasy!",
                        description = "Your productivity journey has started. Check out the Roadmap feature to set your goals.",
                        icon = Icons.Default.CheckCircle,
                        color = Primary
                    )
                }

                item {
                    NotificationHeader("Upcoming Tasks")
                }

                if (uiState.pendingTasksCount > 0) {
                    item {
                        NotificationItem(
                            title = "Tasks Pending",
                            description = "You have ${uiState.pendingTasksCount} tasks waiting for completion. Stay focused!",
                            icon = Icons.Default.CalendarToday,
                            color = Color(0xFFFFD740)
                        )
                    }
                } else {
                    item {
                        Text(
                            "No pending tasks for today. Great job!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = Primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun NotificationItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
