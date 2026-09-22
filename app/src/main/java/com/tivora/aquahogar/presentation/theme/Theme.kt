package com.tivora.aquahogar.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AquaHogarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AquaColorScheme,
        typography = AquaTypography,
        content = content
    )
}
