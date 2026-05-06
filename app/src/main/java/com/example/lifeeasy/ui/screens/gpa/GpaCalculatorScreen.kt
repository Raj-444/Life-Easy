package com.example.lifeeasy.ui.screens.gpa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.domain.model.GpaCourse
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCalculatorScreen(onNavigateBack: () -> Unit) {
    var courses by remember { mutableStateOf(listOf(GpaCourse())) }
    
    val totalCredits = courses.sumOf { it.credits }
    val totalPoints = courses.sumOf { it.credits * it.gradePoint }
    val gpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0

    AuthBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("GPA Calculator", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Optimized for Bangladesh Grading System", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                        }
                    },
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
                    onClick = { courses = courses + GpaCourse() },
                    containerColor = Primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Course")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Summary Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current GPA",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = String.format("%.2f", gpa),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Credits", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                Text(totalCredits.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Quality Points", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                Text(String.format("%.1f", totalPoints), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Courses",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(courses) { course ->
                        CourseItem(
                            course = course,
                            onUpdate = { updated ->
                                courses = courses.map { if (it.id == updated.id) updated else it }
                            },
                            onDelete = {
                                courses = courses.filter { it.id != course.id }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CourseItem(
    course: GpaCourse,
    onUpdate: (GpaCourse) -> Unit,
    onDelete: () -> Unit
) {
    val grades = listOf(
        "A+" to 4.0, "A" to 3.75, "A-" to 3.5,
        "B+" to 3.25, "B" to 3.0, "B-" to 2.75,
        "C+" to 2.5, "C" to 2.25, "D" to 2.0, "F" to 0.0
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = course.name,
                    onValueChange = { onUpdate(course.copy(name = it)) },
                    label = { Text("Course Name", color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    singleLine = true
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = course.credits.toString(),
                        onValueChange = { 
                            val credits = it.toDoubleOrNull() ?: 0.0
                            onUpdate(course.copy(credits = credits))
                        },
                        label = { Text("Credits", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = LocalTextStyle.current.copy(color = Color.White)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            val gradeName = grades.find { it.second == course.gradePoint }?.first ?: "Grade"
                            Text(gradeName, color = Color.White)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            grades.forEach { (label, point) ->
                                DropdownMenuItem(
                                    text = { Text("$label ($point)") },
                                    onClick = {
                                        onUpdate(course.copy(gradePoint = point))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}
