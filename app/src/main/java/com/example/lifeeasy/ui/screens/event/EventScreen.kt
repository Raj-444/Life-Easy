package com.example.lifeeasy.ui.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.domain.model.Event
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import com.example.lifeeasy.ui.theme.spacing
import com.example.lifeeasy.domain.model.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(viewModel: EventViewModel, onNavigateBack: () -> Unit) {
    val events by viewModel.allEvents.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val spacing = MaterialTheme.spacing

    val filteredEvents = remember(events, uiState.selectedDate) {
        events.filter { event ->
            val eventCal = Calendar.getInstance().apply { timeInMillis = event.startTime }
            eventCal.get(Calendar.YEAR) == uiState.selectedDate.get(Calendar.YEAR) &&
                    eventCal.get(Calendar.DAY_OF_YEAR) == uiState.selectedDate.get(Calendar.DAY_OF_YEAR)
        }
    }

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Planner", fontWeight = FontWeight.ExtraBold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddEventSheet() },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                HorizontalDateStrip(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { viewModel.selectDate(it) }
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                TimelineView(
                    events = filteredEvents,
                    subjects = subjects,
                    onDelete = { viewModel.deleteEvent(it) }
                )
            }

            if (uiState.showAddEventSheet) {
                AddEventSheet(
                    subjects = subjects,
                    onDismiss = { viewModel.dismissAddEventSheet() },
                    onSave = { title, subjectId, type, desc, time, reminder, reminderType ->
                        viewModel.saveEvent(title, subjectId, type, desc, time, reminder, reminderType)
                    }
                )
            }
        }
    }
}

@Composable
fun HorizontalDateStrip(
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    val spacing = MaterialTheme.spacing
    val dates = remember {
        val list = mutableListOf<Calendar>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        repeat(30) {
            list.add(cal.clone() as Calendar)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        items(dates) { date ->
            val isSelected = date.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR) &&
                    date.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)

            GlassCard(
                modifier = Modifier
                    .width(60.dp)
                    .height(80.dp)
                    .clickable { onDateSelected(date) },
                containerColor = if (isSelected) Primary.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = SimpleDateFormat("EEE", Locale.getDefault()).format(date.time),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = date.get(Calendar.DAY_OF_MONTH).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineView(
    events: List<Event>,
    subjects: List<com.example.lifeeasy.domain.model.Subject>,
    onDelete: (Event) -> Unit
) {
    val spacing = MaterialTheme.spacing
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = spacing.screenHorizontal,
            vertical = spacing.medium
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        if (events.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No events for this day", color = Color.White.copy(alpha = 0.4f))
                }
            }
        } else {
            items(events.sortedBy { it.startTime }) { event ->
                val subject = subjects.find { it.id == event.subjectId }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    // Time Column
                    Text(
                        text = timeFormat.format(Date(event.startTime)),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.width(50.dp).padding(top = 16.dp)
                    )

                    // Event Card
                    val typeColor = when (event.eventType.lowercase()) {
                        "exam" -> Color(0xFFFF5252)
                        "presentation" -> Color(0xFFFF4081)
                        "lab" -> Color(0xFF448AFF)
                        else -> Accent
                    }

                    GlassCard(
                        modifier = Modifier.weight(1f),
                        containerColor = typeColor.copy(alpha = 0.1f)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = event.eventType.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = typeColor,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { onDelete(event) }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = subject?.name ?: "No Subject",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventSheet(
    subjects: List<com.example.lifeeasy.domain.model.Subject>,
    onDismiss: () -> Unit,
    onSave: (String, String?, String, String, Long, Int, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf<String?>(null) }
    var eventType by remember { mutableStateOf("exam") }
    var description by remember { mutableStateOf("") }
    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF16161E),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Add Event",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedBorderColor = Primary
                )
            )

            // Date & Time Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Date", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                        Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedCalendar.time))
                    }
                }
                OutlinedCard(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Time", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                        Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCalendar.time))
                    }
                }
            }

            // Subject Selector (Simplified for brevity, can use a chips row or dropdown)
            Text("Type", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("exam", "presentation", "lab", "other")) { type ->
                    FilterChip(
                        selected = eventType == type,
                        onClick = { eventType = type },
                        label = { Text(type.uppercase()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Reminder Time
            var selectedReminder by remember { mutableStateOf(15) }
            val reminderOptions = mapOf(
                0 to "At event time",
                5 to "5 mins before",
                15 to "15 mins before",
                30 to "30 mins before",
                60 to "1 hour before"
            )
            Text("Reminder", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reminderOptions.keys.toList()) { mins ->
                    FilterChip(
                        selected = selectedReminder == mins,
                        onClick = { selectedReminder = mins },
                        label = { Text(reminderOptions[mins] ?: "") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Reminder Type
            var reminderType by remember { mutableStateOf("notification") }
            Text("Alert Style", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = reminderType == "notification",
                    onClick = { reminderType = "notification" },
                    label = { Text("Notification") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = reminderType == "alarm",
                    onClick = { reminderType = "alarm" },
                    label = { Text("Alarm (Loud)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Button(
                onClick = {
                    onSave(title, selectedSubjectId, eventType, description, selectedCalendar.timeInMillis, selectedReminder, reminderType)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = title.isNotBlank()
            ) {
                Text("Schedule Event")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedCalendar.timeInMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val newCal = Calendar.getInstance()
                        newCal.timeInMillis = it
                        selectedCalendar = selectedCalendar.apply {
                            set(Calendar.YEAR, newCal.get(Calendar.YEAR))
                            set(Calendar.MONTH, newCal.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, newCal.get(Calendar.DAY_OF_MONTH))
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = selectedCalendar.get(Calendar.MINUTE)
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedCalendar = selectedCalendar.apply {
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }
                    showTimePicker = false
                }) { Text("OK") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
