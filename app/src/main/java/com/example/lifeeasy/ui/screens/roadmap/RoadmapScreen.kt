package com.example.lifeeasy.ui.screens.roadmap

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.data.local.entity.RoadmapEntity
import com.example.lifeeasy.data.local.entity.RoadmapItemEntity

data class RoadmapGoal(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen(
    viewModel: RoadmapViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val goals by viewModel.goals.collectAsState()
    val goalItems by viewModel.goalItems.collectAsState()
    val selectedGoalId by viewModel.selectedGoalId.collectAsState()
    var newCheckpoint by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    
    val progress = if (goals.isEmpty()) 0f else goals.count { it.isCompleted }.toFloat() / goals.size

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("My Roadmap", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Your Journey",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Add your major goals and track your progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Overall Progress", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = Primary,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${(progress * 100).toInt()}% completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Goals",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onToggle = { viewModel.toggleGoalCompletion(goal) },
                        onDelete = { viewModel.deleteGoal(goal) },
                        onClick = {
                            viewModel.selectGoal(goal.id)
                            showSheet = true
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCheckpoint,
                            onValueChange = { newCheckpoint = it },
                            placeholder = { Text("Add a new goal...", color = Color.White.copy(alpha = 0.3f)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newCheckpoint.isNotBlank()) {
                                    viewModel.addGoal(newCheckpoint)
                                    newCheckpoint = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { 
                    showSheet = false
                    viewModel.selectGoal(null)
                },
                sheetState = sheetState,
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color.White
            ) {
                val goal = goals.find { it.id == selectedGoalId }
                if (goal != null) {
                    GoalDetailsSheet(
                        goal = goal,
                        items = goalItems,
                        onAddItem = { topic, date -> viewModel.addItem(goal.id, topic, date) },
                        onToggleItem = { viewModel.toggleItemCompletion(it) },
                        onDeleteItem = { viewModel.deleteItem(it) }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: RoadmapEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (goal.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (goal.isCompleted) Primary else Color.White.copy(alpha = 0.5f)
                )
            }
            Text(
                text = goal.title,
                color = if (goal.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun GoalDetailsSheet(
    goal: RoadmapEntity,
    items: List<RoadmapItemEntity>,
    onAddItem: (String, String) -> Unit,
    onToggleItem: (RoadmapItemEntity) -> Unit,
    onDeleteItem: (RoadmapItemEntity) -> Unit
) {
    var newTopic by remember { mutableStateOf("") }
    var newDate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = goal.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Text(
            text = "Sub-tasks & Topics",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            Text(
                "No sub-tasks yet. Break down your goal into smaller topics!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onToggleItem(item) }) {
                        Icon(
                            imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (item.isCompleted) Primary else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.topic,
                            color = if (item.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        if (item.date.isNotBlank()) {
                            Text(item.date, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f))
                        }
                    }
                    IconButton(onClick = { onDeleteItem(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Add Sub-task", style = MaterialTheme.typography.titleSmall, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = newTopic,
            onValueChange = { newTopic = it },
            placeholder = { Text("Topic Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newDate,
            onValueChange = { newDate = it },
            placeholder = { Text("Target Date (e.g. 25th Oct)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (newTopic.isNotBlank()) {
                    onAddItem(newTopic, newDate)
                    newTopic = ""
                    newDate = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add Sub-task")
        }
    }
}
