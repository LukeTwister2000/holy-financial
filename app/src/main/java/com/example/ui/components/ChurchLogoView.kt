package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ChurchLogoView(
    logoPath: String?,
    size: Dp = 48.dp,
    borderWidth: Dp = 1.5.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .border(borderWidth, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!logoPath.isNullOrEmpty() && File(logoPath).exists()) {
            AsyncImage(
                model = File(logoPath),
                contentDescription = "Logo da Igreja",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Church,
                contentDescription = "Logo Padrão",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}
