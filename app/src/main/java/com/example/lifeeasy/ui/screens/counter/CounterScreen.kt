package com.example.lifeeasy.ui.screens.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.domain.model.Counter
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import com.example.lifeeasy.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    viewModel: CounterViewModel,
    onCounterClick: (String) -> Unit
) {
    val counters by viewModel.allCounters.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val spacing = MaterialTheme.spacing
    val haptic = LocalHapticFeedback.current

    AuthBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Counters", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${counters.size} active trackers", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddDialog() },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Counter")
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (counters.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔢", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No counters yet", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text("Track anything by tapping +", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                    contentPadding = PaddingValues(bottom = 100.dp, top = spacing.small)
                ) {
                    items(counters, key = { it.id }) { counter ->
                        CounterGridItem(
                            counter = counter,
                            onIncrement = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.increment(counter.id) 
                            },
                            onDecrement = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.decrement(counter.id) 
                            },
                            onClick = { onCounterClick(counter.id) }
                        )
                    }
                }
            }
        }

        if (uiState.showAddDialog) {
            AddCounterDialog(
                onDismiss = { viewModel.dismissAddDialog() },
                onAdd = { name, initial, target ->
                    viewModel.addCounter(name, initial, target)
                }
            )
        }
    }
}

@Composable
fun CounterGridItem(
    counter: Counter,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable { onClick() },
        blur = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = counter.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = counter.count.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp
                ),
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallControlButton(
                    icon = Icons.Default.Remove,
                    color = Color(0xFFF44336),
                    onClick = onDecrement
                )
                
                SmallControlButton(
                    icon = Icons.Default.Add,
                    color = Color(0xFF4CAF50),
                    onClick = onIncrement
                )
            }
            
            if (counter.targetCount != null) {
                val progress = (counter.count.toFloat() / counter.targetCount.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = Primary,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun SmallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = { onClick() },
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AddCounterDialog(onDismiss: () -> Unit, onAdd: (String, Int, Int?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var initial by remember { mutableStateOf("0") }
    var target by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onAdd(name, initial.toIntOrNull() ?: 0, target.toIntOrNull()) }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("New Counter Tracker") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("What are you tracking?") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = initial,
                    onValueChange = { initial = it },
                    label = { Text("Initial Count") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Goal/Target (Optional)") },
                    singleLine = true
                )
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
