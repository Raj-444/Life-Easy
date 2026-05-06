package com.example.lifeeasy.ui.screens.debt

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeeasy.domain.model.DebtTransaction
import com.example.lifeeasy.domain.model.Person
import com.example.lifeeasy.ui.components.AuthBackground
import com.example.lifeeasy.ui.components.GlassCard
import com.example.lifeeasy.ui.theme.Primary
import com.example.lifeeasy.ui.theme.Accent
import com.example.lifeeasy.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(viewModel: DebtViewModel, onNavigateBack: () -> Unit) {
    val persons by viewModel.persons.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val standaloneExpenses by viewModel.standaloneExpenses.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val spacing = MaterialTheme.spacing

    // Summary calculation
    val totalLent = allTransactions.filter { it.type == "given" }.sumOf { it.amount }
    val totalBorrowed = allTransactions.filter { it.type == "received" }.sumOf { it.amount }

    AuthBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lend & Borrow", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { 
                        if (selectedTab == 0) viewModel.showAddPersonDialog() 
                        else viewModel.showAddExpenseDialog() 
                    },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Default.PersonAdd else Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. Summary Pie Chart Section
                SummaryChartSection(totalLent, totalBorrowed)

                // 1.5 Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Primary
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Contacts", color = if (selectedTab == 0) Primary else Color.White.copy(alpha = 0.6f)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Expenses", color = if (selectedTab == 1) Primary else Color.White.copy(alpha = 0.6f)) }
                    )
                }

                Spacer(modifier = Modifier.height(spacing.small))

                // 2. Main Content
                if (selectedTab == 0) {
                    ContactsList(persons, allTransactions, viewModel, spacing)
                } else {
                    StandaloneExpensesList(standaloneExpenses, viewModel, spacing)
                }
            }
        }

        // Dialogs
        if (uiState.showAddPersonDialog) {
            AddPersonDialog(
                onDismiss = { viewModel.dismissAddPersonDialog() },
                onAdd = { name -> viewModel.addPerson(name) }
            )
        }

        if (uiState.showAddExpenseDialog) {
            AddStandaloneExpenseDialog(
                onDismiss = { viewModel.dismissAddExpenseDialog() },
                onAdd = { title, amount, isBorrow -> viewModel.addStandaloneExpense(title, amount, isBorrow) }
            )
        }

        uiState.selectedPersonId?.let { personId ->
            val person = persons.find { it.id == personId }
            if (person != null) {
                PersonTransactionsOverlay(
                    person = person,
                    viewModel = viewModel,
                    onDismiss = { viewModel.selectPerson(null) }
                )
            }
        }
    }
}

@Composable
fun SummaryChartSection(lent: Double, borrowed: Double) {
    val spacing = MaterialTheme.spacing
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing.medium),
        blur = 20.dp
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                LendBorrowPieChart(lent, borrowed)
            }
            
            Spacer(modifier = Modifier.width(spacing.large))
            
            Column {
                SummaryRow(label = "Total Lent", amount = lent, color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(spacing.small))
                SummaryRow(label = "Total Borrowed", amount = borrowed, color = Color(0xFFF44336))
                
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
                
                val net = lent - borrowed
                Text(
                    text = if (net >= 0) "Net Receivable" else "Net Payable",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "৳${String.format("%,.0f", Math.abs(net))}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun LendBorrowPieChart(lent: Double, borrowed: Double) {
    val total = lent + borrowed
    if (total == 0.0) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color.White.copy(alpha = 0.1f), style = Stroke(width = 20f))
        }
        return
    }

    val lentAngle = (lent / total * 360f).toFloat()
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 25f
        val size = Size(size.width - strokeWidth, size.height - strokeWidth)
        
        // Borrowed arc (Red)
        drawArc(
            color = Color(0xFFF44336).copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = size,
            topLeft = Offset(strokeWidth/2, strokeWidth/2)
        )
        
        // Lent arc (Green)
        drawArc(
            color = Color(0xFF4CAF50),
            startAngle = -90f,
            sweepAngle = lentAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = size,
            topLeft = Offset(strokeWidth/2, strokeWidth/2)
        )
    }
}

@Composable
fun SummaryRow(label: String, amount: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
            Text("৳${String.format("%,.0f", amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun PersonDebtCard(
    person: Person,
    balance: Double,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        blur = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(person.name.take(1).uppercase(), color = Primary, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(person.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                if (balance > 0) {
                    Text("Owes you ৳${String.format("%,.0f", balance)}", color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodySmall)
                } else if (balance < 0) {
                    Text("You owe ৳${String.format("%,.0f", -balance)}", color = Color(0xFFF44336), style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("Settled up", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.bodySmall)
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.3f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonTransactionsOverlay(
    person: Person,
    viewModel: DebtViewModel,
    onDismiss: () -> Unit
) {
    val transactions by viewModel.selectedPersonTransactions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val spacing = MaterialTheme.spacing
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1926),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(person.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                Button(
                    onClick = { viewModel.showAddTransactionDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Record")
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.medium))
            
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No transactions yet.", color = Color.White.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    items(transactions) { tx ->
                        TransactionItem(tx, dateFormat) { viewModel.deleteTransaction(tx) }
                    }
                }
            }
        }
    }

    if (uiState.showAddTransactionDialog) {
        AddTransactionDialog(
            onDismiss = { viewModel.dismissAddTransactionDialog() },
            onAdd = { amount, type, note -> viewModel.addTransaction(amount, type, note) }
        )
    }
}

@Composable
fun TransactionItem(tx: DebtTransaction, dateFormat: SimpleDateFormat, onDelete: () -> Unit) {
    val isGiven = tx.type == "given"
    val color = if (isGiven) Color(0xFF4CAF50) else Color(0xFFF44336)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isGiven) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(if (isGiven) "Lent" else "Borrowed", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
            if (tx.note.isNotBlank()) {
                Text(tx.note, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
            }
            Text(dateFormat.format(Date(tx.date)), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f))
        }
        Text("৳${String.format("%,.0f", tx.amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = Color.White.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun ContactsList(
    persons: List<Person>,
    allTransactions: List<DebtTransaction>,
    viewModel: DebtViewModel,
    spacing: com.example.lifeeasy.ui.theme.Spacing
) {
    if (persons.isEmpty()) {
        EmptyDebtState("Add a person to track debts")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            contentPadding = PaddingValues(bottom = 100.dp, top = spacing.small)
        ) {
            items(persons, key = { it.id }) { person ->
                val personTransactions = allTransactions.filter { it.personId == person.id }
                val balance = personTransactions.sumOf { 
                    if (it.type == "given") it.amount else -it.amount 
                }
                PersonDebtCard(
                    person = person,
                    balance = balance,
                    onClick = { viewModel.selectPerson(person.id) },
                    onDelete = { viewModel.deletePerson(person) }
                )
            }
        }
    }
}

@Composable
fun StandaloneExpensesList(
    expenses: List<com.example.lifeeasy.data.local.entity.ExpenseEntity>,
    viewModel: DebtViewModel,
    spacing: com.example.lifeeasy.ui.theme.Spacing
) {
    if (expenses.isEmpty()) {
        EmptyDebtState("Add a quick expense without a contact")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            contentPadding = PaddingValues(bottom = 100.dp, top = spacing.small)
        ) {
            items(expenses, key = { it.id }) { expense ->
                StandaloneExpenseCard(
                    expense = expense,
                    onDelete = { viewModel.deleteStandaloneExpense(expense) }
                )
            }
        }
    }
}

@Composable
fun StandaloneExpenseCard(
    expense: com.example.lifeeasy.data.local.entity.ExpenseEntity,
    onDelete: () -> Unit
) {
    val color = if (expense.isBorrow) Color(0xFFF44336) else Color(0xFF4CAF50)
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (expense.isBorrow) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    if (expense.isBorrow) "Borrowed" else "Lent",
                    color = color.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Text(
                "৳${String.format("%,.0f", expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = color
            )
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun AddStandaloneExpenseDialog(onDismiss: () -> Unit, onAdd: (String, Double, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isBorrow by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = !isBorrow,
                        onClick = { isBorrow = false },
                        label = { Text("Lent") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f))
                    )
                    FilterChip(
                        selected = isBorrow,
                        onClick = { isBorrow = true },
                        label = { Text("Borrowed") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFF44336).copy(alpha = 0.2f))
                    )
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. Snacks)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (৳)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(title, amount.toDoubleOrNull() ?: 0.0, isBorrow) },
                enabled = title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun EmptyDebtState(subtitle: String = "Add a person to track debts") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("💸", style = androidx.compose.ui.text.TextStyle(fontSize = 60.sp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your ledger is clean", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun AddPersonDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Contact") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Person Name") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onAdd(name) }, enabled = name.isNotBlank()) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun AddTransactionDialog(onDismiss: () -> Unit, onAdd: (Double, String, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("given") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = type == "given",
                        onClick = { type = "given" },
                        label = { Text("I Lent") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f))
                    )
                    FilterChip(
                        selected = type == "received",
                        onClick = { type = "received" },
                        label = { Text("I Borrowed") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFF44336).copy(alpha = 0.2f))
                    )
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (৳)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (e.g. For Pizza)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(amount.toDoubleOrNull() ?: 0.0, type, note) },
                enabled = amount.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
