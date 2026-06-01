package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = NavySecondary,
    tertiary = LightGoldAccent,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = NavyPrimary,
    onSecondary = LightText,
    onTertiary = NavyPrimary,
    onBackground = LightText,
    onSurface = LightText,
    error = ErrorRed,
    primaryContainer = NavySecondary,
    onPrimaryContainer = GoldAccent
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    secondary = NavySecondary,
    tertiary = GoldAccent
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // default to true for premium look
  accentColor: androidx.compose.ui.graphics.Color = GoldAccent,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val customDarkScheme = darkColorScheme(
    primary = accentColor,
    secondary = NavySecondary,
    tertiary = accentColor.copy(alpha = 0.8f),
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = NavyPrimary,
    onSecondary = LightText,
    onTertiary = NavyPrimary,
    onBackground = LightText,
    onSurface = LightText,
    error = ErrorRed,
    primaryContainer = NavySecondary,
    onPrimaryContainer = accentColor
  )

  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> customDarkScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
