package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.ui.ChurchAppScreen
import com.example.ui.ChurchViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.GoldAccent

class MainActivity : ComponentActivity() {
  private val viewModel: ChurchViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
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
}

