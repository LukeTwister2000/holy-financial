package com.example.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.desktop.ui.ChurchAppScreen
import com.example.desktop.ui.ChurchViewModel
import com.example.desktop.ui.theme.MyApplicationTheme
import com.example.desktop.ui.theme.GoldAccent

fun main() = application {
  val viewModel = remember { ChurchViewModel() }
  
  Window(
    onCloseRequest = ::exitApplication,
    title = "Holy Financial - Gerenciador de Finanças",
    state = rememberWindowState(width = 1200.dp, height = 800.dp)
  ) {
    val accentByPref by viewModel.accentColor.collectAsState()
    val composeAccentColor = when (accentByPref) {
      "GOLD" -> Color(0xFFD4AF37)
      "GREEN" -> Color(0xFF27AE60)
      "BLUE" -> Color(0xFF3498DB)
      "PURPLE" -> Color(0xFF9855F7)
      else -> GoldAccent
    }

    MyApplicationTheme(accentColor = composeAccentColor) {
      ChurchAppScreen(viewModel = viewModel)
    }
  }
}
