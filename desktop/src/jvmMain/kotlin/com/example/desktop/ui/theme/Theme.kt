package com.example.desktop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GoldAccent = Color(0xFFD4AF37)
val GreenAccent = Color(0xFF27AE60)
val BlueAccent = Color(0xFF3498DB)
val PurpleAccent = Color(0xFF9855F7)

@Composable
fun MyApplicationTheme(
  useDarkTheme: Boolean = isSystemInDarkTheme(),
  accentColor: Color = GoldAccent,
  content: @Composable () -> Unit
) {
  val lightColorScheme = lightColorScheme(
    primary = accentColor,
    secondary = accentColor,
    tertiary = accentColor
  )
  
  val darkColorScheme = darkColorScheme(
    primary = accentColor,
    secondary = accentColor,
    tertiary = accentColor
  )

  val colorScheme = if (useDarkTheme) darkColorScheme else lightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    content = content
  )
}
