package com.example.lifeeasy.ui.screens.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockScreen(onNavigateBack: () -> Unit) {
    var time by remember { mutableStateOf(Calendar.getInstance()) }
    var showAnalog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            time = Calendar.getInstance()
            delay(1000)
        }
    }

    Scaffold(
        containerColor = Color.Black,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            // Subtle Back Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White.copy(alpha = 0.3f))
            }

            // Mode Toggle
            IconButton(
                onClick = { showAnalog = !showAnalog },
                modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Schedule, contentDescription = "Toggle Mode", tint = Color.White.copy(alpha = 0.3f))
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (showAnalog) {
                    AnalogClock(time)
                } else {
                    DigitalClock(time)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Date
                val dateSdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
                Text(
                    text = dateSdf.format(time.time),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun DigitalClock(time: Calendar) {
    val timeSdf = SimpleDateFormat("hh:mm", Locale.getDefault())
    val amPmSdf = SimpleDateFormat("a", Locale.getDefault())
    val secondsSdf = SimpleDateFormat("ss", Locale.getDefault())

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = timeSdf.format(time.time),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 8.sp
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = amPmSdf.format(time.time),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676)
                )
            )
            Text(
                text = secondsSdf.format(time.time),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun AnalogClock(time: Calendar) {
    val hours = time.get(Calendar.HOUR)
    val minutes = time.get(Calendar.MINUTE)
    val seconds = time.get(Calendar.SECOND)

    Canvas(modifier = Modifier.size(300.dp)) {
        val center = center
        val radius = size.minDimension / 2

        // Clock Face
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = radius,
            center = center
        )

        // Hour Markers
        for (i in 0 until 12) {
            val angle = i * 30f
            rotate(angle) {
                drawLine(
                    color = Color.White,
                    start = center.copy(y = center.y - radius + 10.dp.toPx()),
                    end = center.copy(y = center.y - radius + 25.dp.toPx()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Second Hand
        rotate(seconds * 6f) {
            drawLine(
                color = Color.Red,
                start = center,
                end = center.copy(y = center.y - radius + 20.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Minute Hand
        rotate(minutes * 6f + seconds * 0.1f) {
            drawLine(
                color = Color.White,
                start = center,
                end = center.copy(y = center.y - radius + 40.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Hour Hand
        rotate(hours * 30f + minutes * 0.5f) {
            drawLine(
                color = Color(0xFF00E676),
                start = center,
                end = center.copy(y = center.y - radius + 80.dp.toPx()),
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Center Pin
        drawCircle(color = Color.White, radius = 6.dp.toPx(), center = center)
    }
}
