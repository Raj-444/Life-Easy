package com.example.lifeeasy.ui.screens.counter

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.domain.model.Counter
import com.example.lifeeasy.domain.model.CounterHistory
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import com.example.lifeeasy.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterDetailScreen(
    counterId: String,
    viewModel: CounterViewModel,
    onBack: () -> Unit
) {
    val counters by viewModel.allCounters.collectAsState()
    val counter = counters.find { it.id == counterId }
    val history by viewModel.getHistoryForCounter(counterId).collectAsState(initial = emptyList())
    val haptic = LocalHapticFeedback.current
    val spacing = MaterialTheme.spacing
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy · hh:mm:ss a", Locale.getDefault()) }

    AuthBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(counter?.label ?: "Counter Detail", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (counter == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Counter Card
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.medium),
                        blur = 20.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.large),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                counter.count.toString(),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-2).sp
                                ),
                                color = Color.White
                            )
                            
                            Spacer(modifier = Modifier.height(spacing.medium))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ControlButton(
                                    icon = Icons.Default.Remove,
                                    color = Color(0xFFF44336),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.decrement(counter.id)
                                    }
                                )
                                
                                ControlButton(
                                    icon = Icons.Default.Add,
                                    color = Color(0xFF4CAF50),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.increment(counter.id)
                                    }
                                )
                            }
                        }
                    }

                    // History Title
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Activity Log",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // History List
                    GlassCard(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        blur = 15.dp
                    ) {
                        if (history.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No activity yet", color = Color.White.copy(alpha = 0.5f))
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(spacing.medium),
                                verticalArrangement = Arrangement.spacedBy(spacing.small)
                            ) {
                                items(history) { record ->
                                    HistoryItem(record, dateFormat)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(spacing.medium))
                }
            }
        }
    }
}

@Composable
fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.2f),
        contentColor = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun HistoryItem(record: CounterHistory, dateFormat: SimpleDateFormat) {
    val isPositive = record.newCount > record.oldCount
    val accentColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (record.changeType == "INITIAL") "Counter Created" else "${record.oldCount} → ${record.newCount}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = dateFormat.format(Date(record.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
        
        Text(
            text = if (isPositive) "+1" else "-1",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
    }
}
