package com.example.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChurchAppScreen(viewModel: ChurchViewModel) {
  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Dashboard", "Despesas", "Receitas", "Configurações")

  Row(modifier = Modifier.fillMaxSize()) {
    // Sidebar
    NavigationRail(
      modifier = Modifier.width(200.dp)
    ) {
      tabs.forEachIndexed { index, title ->
        NavigationRailItem(
          icon = {
            when (index) {
              0 -> Icon(Icons.Default.Home, contentDescription = title)
              1 -> Icon(Icons.Default.ShoppingCart, contentDescription = title)
              2 -> Icon(Icons.Default.AttachMoney, contentDescription = title)
              3 -> Icon(Icons.Default.Settings, contentDescription = title)
              else -> Icon(Icons.Default.Home, contentDescription = title)
            }
          },
          label = { Text(title) },
          selected = selectedTab == index,
          onClick = { selectedTab = index }
        )
      }
    }

    // Content
    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
      when (selectedTab) {
        0 -> DashboardTab(viewModel)
        1 -> ExpensesTab(viewModel)
        2 -> EarningsTab(viewModel)
        3 -> SettingsTab(viewModel)
      }
    }
  }
}

@Composable
fun DashboardTab(viewModel: ChurchViewModel) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      "Dashboard",
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold
    )

    val expenses by viewModel.expenses
    val earnings by viewModel.earnings

    val totalExpenses = expenses.sumOf { it.amount }
    val totalEarnings = earnings.sumOf { it.amount }
    val balance = totalEarnings - totalExpenses

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      StatCard("Receitas", "R$ ${String.format("%.2f", totalEarnings)}", Color(0xFF27AE60))
      StatCard("Despesas", "R$ ${String.format("%.2f", totalExpenses)}", Color(0xFFE74C3C))
      StatCard("Saldo", "R$ ${String.format("%.2f", balance)}", Color(0xFF3498DB))
    }

    Text(
      "Últimas Transações",
      fontSize = 18.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun ExpensesTab(viewModel: ChurchViewModel) {
  var showDialog by remember { mutableStateOf(false) }
  var category by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var amount by remember { mutableStateOf("") }
  var date by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Despesas", fontSize = 28.sp, fontWeight = FontWeight.Bold)
      Button(onClick = { showDialog = true }) {
        Icon(Icons.Default.Add, contentDescription = "Adicionar")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Adicionar Despesa")
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    val expenses by viewModel.expenses
    if (expenses.isNotEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
      ) {
        expenses.forEach { expense ->
          ExpenseCard(expense, onDelete = { viewModel.removeExpense(expense.id) })
        }
      }
    } else {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text("Nenhuma despesa registrada")
      }
    }

    if (showDialog) {
      AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text("Adicionar Despesa") },
        text = {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TextField(value = category, onValueChange = { category = it }, label = { Text("Categoria") })
            TextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") })
            TextField(value = amount, onValueChange = { amount = it }, label = { Text("Valor") })
            TextField(value = date, onValueChange = { date = it }, label = { Text("Data") })
          }
        },
        confirmButton = {
          Button(onClick = {
            if (category.isNotEmpty() && amount.toDoubleOrNull() != null) {
              viewModel.addExpense(
                Expense(
                  category = category,
                  description = description,
                  amount = amount.toDouble(),
                  date = date
                )
              )
              showDialog = false
              category = ""
              description = ""
              amount = ""
              date = ""
            }
          }) {
            Text("Adicionar")
          }
        },
        dismissButton = {
          Button(onClick = { showDialog = false }) {
            Text("Cancelar")
          }
        }
      )
    }
  }
}

@Composable
fun EarningsTab(viewModel: ChurchViewModel) {
  var showDialog by remember { mutableStateOf(false) }
  var source by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var amount by remember { mutableStateOf("") }
  var date by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Receitas", fontSize = 28.sp, fontWeight = FontWeight.Bold)
      Button(onClick = { showDialog = true }) {
        Icon(Icons.Default.Add, contentDescription = "Adicionar")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Adicionar Receita")
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    val earnings by viewModel.earnings
    if (earnings.isNotEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
      ) {
        earnings.forEach { earning ->
          EarningCard(earning, onDelete = { viewModel.removeEarning(earning.id) })
        }
      }
    } else {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text("Nenhuma receita registrada")
      }
    }

    if (showDialog) {
      AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text("Adicionar Receita") },
        text = {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TextField(value = source, onValueChange = { source = it }, label = { Text("Origem") })
            TextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") })
            TextField(value = amount, onValueChange = { amount = it }, label = { Text("Valor") })
            TextField(value = date, onValueChange = { date = it }, label = { Text("Data") })
          }
        },
        confirmButton = {
          Button(onClick = {
            if (source.isNotEmpty() && amount.toDoubleOrNull() != null) {
              viewModel.addEarning(
                Earning(
                  source = source,
                  description = description,
                  amount = amount.toDouble(),
                  date = date
                )
              )
              showDialog = false
              source = ""
              description = ""
              amount = ""
              date = ""
            }
          }) {
            Text("Adicionar")
          }
        },
        dismissButton = {
          Button(onClick = { showDialog = false }) {
            Text("Cancelar")
          }
        }
      )
    }
  }
}

@Composable
fun SettingsTab(viewModel: ChurchViewModel) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text("Configurações", fontSize = 28.sp, fontWeight = FontWeight.Bold)

    Text("Tema", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

    val colors = listOf("GOLD", "GREEN", "BLUE", "PURPLE")
    colors.forEach { color ->
      Button(
        onClick = { viewModel.setAccentColor(color) },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(color)
      }
    }
  }
}

@Composable
fun StatCard(title: String, value: String, backgroundColor: Color) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(100.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.Center
    ) {
      Text(title, color = Color.White, fontSize = 14.sp)
      Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun ExpenseCard(expense: Expense, onDelete: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(expense.category, fontWeight = FontWeight.Bold)
        Text(expense.description, fontSize = 12.sp)
        Text(expense.date, fontSize = 10.sp)
      }
      Text("R$ ${String.format("%.2f", expense.amount)}", fontWeight = FontWeight.Bold, color = Color(0xFFE74C3C))
      IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = "Deletar")
      }
    }
  }
}

@Composable
fun EarningCard(earning: Earning, onDelete: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(earning.source, fontWeight = FontWeight.Bold)
        Text(earning.description, fontSize = 12.sp)
        Text(earning.date, fontSize = 10.sp)
      }
      Text("R$ ${String.format("%.2f", earning.amount)}", fontWeight = FontWeight.Bold, color = Color(0xFF27AE60))
      IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = "Deletar")
      }
    }
  }
}
