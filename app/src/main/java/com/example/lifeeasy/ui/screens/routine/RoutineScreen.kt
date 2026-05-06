package com.example.lifeeasy.ui.screens.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.domain.model.RoutineItem
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(viewModel: RoutineViewModel, onNavigateBack: () -> Unit) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val routineItems by viewModel.routineItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val days = listOf(
        Calendar.SUNDAY to "Sun",
        Calendar.MONDAY to "Mon",
        Calendar.TUESDAY to "Tue",
        Calendar.WEDNESDAY to "Wed",
        Calendar.THURSDAY to "Thu",
        Calendar.FRIDAY to "Fri",
        Calendar.SATURDAY to "Sat"
    )

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Class Routine", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Class")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Day Selector
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(days) { (dayInt, dayName) ->
                        DayChip(
                            name = dayName,
                            isSelected = selectedDay == dayInt,
                            onClick = { viewModel.selectDay(dayInt) }
                        )
                    }
                }

                if (routineItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No classes scheduled for today", color = Color.White.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(routineItems) { item ->
                            RoutineItemCard(item = item, onDelete = { viewModel.deleteRoutineItem(item) })
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddRoutineDialog(
            selectedDay = selectedDay,
            onDismiss = { showAddDialog = false },
            onConfirm = { item ->
                viewModel.addRoutineItem(item)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DayChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Primary else Color.White.copy(alpha = 0.1f),
        contentColor = Color.White
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun RoutineItemCard(item: RoutineItem, onDelete: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Primary)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.subjectName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${item.startTime} - ${item.endTime}", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                if (item.room.isNotEmpty()) {
                    Text("Room: ${item.room}", color = Accent, fontSize = 12.sp)
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun AddRoutineDialog(selectedDay: Int, onDismiss: () -> Unit, onConfirm: (RoutineItem) -> Unit) {
    var subject by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
                OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start Time (e.g. 09:00 AM)") })
                OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("End Time (e.g. 10:30 AM)") })
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room/Link") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotEmpty()) {
                        onConfirm(RoutineItem(dayOfWeek = selectedDay, subjectName = subject, startTime = startTime, endTime = endTime, room = room))
                    }
                },
                enabled = subject.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
