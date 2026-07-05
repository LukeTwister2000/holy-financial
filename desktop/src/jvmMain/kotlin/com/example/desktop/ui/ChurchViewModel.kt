package com.example.desktop.ui

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ChurchViewModel {
  private val _accentColor = MutableStateFlow("GOLD")
  val accentColor: StateFlow<String> = _accentColor.asStateFlow()

  private val _showDialog = mutableStateOf(false)
  val showDialog = _showDialog

  private val _expenses = mutableStateOf(emptyList<Expense>())
  val expenses = _expenses

  private val _earnings = mutableStateOf(emptyList<Earning>())
  val earnings = _earnings

  private val dataDir = File(System.getProperty("user.home"), ".holy-financial")

  init {
    ensureDataDirectory()
    loadData()
  }

  private fun ensureDataDirectory() {
    if (!dataDir.exists()) {
      dataDir.mkdirs()
    }
  }

  private fun loadData() {
    // Carregar dados do arquivo local
    // Implementar persistência de dados
  }

  fun addExpense(expense: Expense) {
    val currentList = _expenses.value.toMutableList()
    currentList.add(expense)
    _expenses.value = currentList
    saveData()
  }

  fun addEarning(earning: Earning) {
    val currentList = _earnings.value.toMutableList()
    currentList.add(earning)
    _earnings.value = currentList
    saveData()
  }

  fun removeExpense(id: String) {
    _expenses.value = _expenses.value.filter { it.id != id }
    saveData()
  }

  fun removeEarning(id: String) {
    _earnings.value = _earnings.value.filter { it.id != id }
    saveData()
  }

  fun setAccentColor(color: String) {
    _accentColor.value = color
    savePreferences()
  }

  private fun saveData() {
    // Salvar dados em arquivo local (JSON)
  }

  private fun savePreferences() {
    val prefsFile = File(dataDir, "prefs.txt")
    prefsFile.writeText("accent_color=${_accentColor.value}")
  }
}

data class Expense(
  val id: String = java.util.UUID.randomUUID().toString(),
  val category: String,
  val description: String,
  val amount: Double,
  val date: String,
  val paymentMethod: String = "Cash"
)

data class Earning(
  val id: String = java.util.UUID.randomUUID().toString(),
  val source: String,
  val description: String,
  val amount: Double,
  val date: String
)
