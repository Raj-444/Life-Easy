package com.example.lifeeasy.ui.screens.task

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lifeeasy.domain.model.Task
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import com.example.lifeeasy.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

// Priority color palette
val PriorityLow = Color(0xFF4CAF50)       // Green
val PriorityMedium = Color(0xFFFF9800)     // Orange
val PriorityHigh = Color(0xFFF44336)       // Red

fun priorityColor(priority: Int): Color = when (priority) {
    2 -> PriorityHigh
    1 -> PriorityMedium
    else -> PriorityLow
}

fun priorityLabel(priority: Int): String = when (priority) {
    2 -> "High"
    1 -> "Medium"
    else -> "Low"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val spacing = MaterialTheme.spacing

    // Show snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    AuthBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Tasks",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "${tasks.count { !it.isCompleted }} tasks remaining",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddDialog() },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. Sticky Quick Input Bar
                QuickInputBar(
                    onAdd = { title -> viewModel.addTask(title, "", 1, null, null) }
                )

                // 2. Scrollable Filter Chips
                FilterChipRow(
                    selectedFilter = uiState.activeFilter,
                    onFilterSelected = { viewModel.setFilter(it) }
                )

                val filteredTasks = remember(tasks, uiState.activeFilter) {
                    when (uiState.activeFilter) {
                        TaskFilter.ALL -> tasks.sortedBy { it.isCompleted }
                        TaskFilter.TODAY -> {
                            val today = Calendar.getInstance()
                            tasks.filter { task ->
                                task.dueDate != null && isSameDay(task.dueDate, today)
                            }.sortedBy { it.isCompleted }
                        }
                        TaskFilter.UPCOMING -> {
                            val now = System.currentTimeMillis()
                            tasks.filter { task ->
                                task.dueDate != null && task.dueDate > now && !isToday(task.dueDate)
                            }.sortedBy { it.dueDate }
                        }
                        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
                    }
                }

                if (filteredTasks.isEmpty()) {
                    EmptyStateView(uiState.activeFilter)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.small),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            SwipeableTaskCard(
                                task = task,
                                onToggle = { viewModel.toggleComplete(task) },
                                onDelete = { viewModel.deleteTask(task) },
                                onClick = { viewModel.showEditDialog(task) }
                            )
                        }
                    }
                }
            }
        }

        // Add / Edit dialog (existing functionality preserved)
        if (uiState.showAddDialog) {
            AddEditTaskDialog(
                task = uiState.editingTask,
                onDismiss = { viewModel.dismissDialog() },
                onSave = { title, desc, priority, dueDate, reminderTime ->
                    val existing = uiState.editingTask
                    if (existing != null) {
                        viewModel.updateTask(
                            existing.copy(
                                title = title,
                                description = desc,
                                priority = priority,
                                dueDate = dueDate,
                                reminderTime = reminderTime
                            )
                        )
                    } else {
                        viewModel.addTask(title, desc, priority, dueDate, reminderTime)
                    }
                }
            )
        }
    }
}

@Composable
fun QuickInputBar(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val spacing = MaterialTheme.spacing

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.medium, vertical = spacing.small),
        blur = 15.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { 
                    Text(
                        "Quickly add a task...", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    ) 
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (text.isNotBlank()) {
                        onAdd(text)
                        text = ""
                    }
                    focusManager.clearFocus()
                })
            )
            
            if (text.isNotBlank()) {
                IconButton(onClick = {
                    onAdd(text)
                    text = ""
                    focusManager.clearFocus()
                }) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Add",
                        tint = Primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipRow(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    val spacing = MaterialTheme.spacing
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.small),
        contentPadding = PaddingValues(horizontal = spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TaskFilter.entries.toTypedArray()) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { 
                    Text(
                        filter.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White.copy(alpha = 0.1f),
                    labelColor = Color.White.copy(alpha = 0.7f)
                ),
                border = null,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun EmptyStateView(filter: TaskFilter) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                when(filter) {
                    TaskFilter.COMPLETED -> "🏆"
                    TaskFilter.TODAY -> "🌟"
                    else -> "📋"
                },
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                when(filter) {
                    TaskFilter.COMPLETED -> "No completed tasks"
                    TaskFilter.TODAY -> "All caught up for today!"
                    else -> "No tasks found"
                },
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                "Try adding a new task",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskCard(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggle()
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            SwipeBackground(dismissState)
        },
        content = {
            TaskCard(task = task, onToggle = onToggle, onClick = onClick)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.dismissDirection
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50).copy(alpha = 0.9f)
            SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336).copy(alpha = 0.9f)
            else -> Color.Transparent
        },
        label = "swipe_bg"
    )
    val icon = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.CheckCircle
        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
        else -> Icons.Default.Delete
    }
    val alignment = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterEnd
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1.2f,
        label = "swipe_icon_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.scale(scale),
            tint = Color.White
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val pColor = priorityColor(task.priority)
    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        blur = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle
                    else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Toggle",
                    tint = if (task.isCompleted) Color(0xFF4CAF50)
                    else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) Color.White.copy(alpha = 0.5f)
                    else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(pColor)
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Text(
                        text = priorityLabel(task.priority),
                        style = MaterialTheme.typography.labelSmall,
                        color = pColor,
                        fontWeight = FontWeight.Bold
                    )

                    if (task.dueDate != null) {
                        Text(
                            " · ${dateFormat.format(Date(task.dueDate))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            
            if (task.reminderTime != null && !task.isCompleted) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = "Reminder",
                    modifier = Modifier.size(18.dp),
                    tint = Accent
                )
            }
        }
    }
}

// Utility functions
fun isSameDay(time1: Long, cal2: Calendar): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isToday(time: Long): Boolean = isSameDay(time, Calendar.getInstance())
