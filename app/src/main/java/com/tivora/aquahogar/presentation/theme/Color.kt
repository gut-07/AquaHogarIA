package com.tivora.aquahogar.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val DeepWater = Color(0xFF0B3A4A)
val Lagoon = Color(0xFF1565C0)
val Aqua = Color(0xFF26C6DA)
val Foam = Color(0xFFE0F7FA)
val Sand = Color(0xFFFFF8E1)
val AlertHigh = Color(0xFFC62828)
val AlertWarn = Color(0xFFEF6C00)
val AlertOk = Color(0xFF2E7D32)

val AquaColorScheme = darkColorScheme(
    primary = Aqua,
    onPrimary = DeepWater,
    primaryContainer = Color(0xFF0E5C73),
    onPrimaryContainer = Foam,
    secondary = Color(0xFF80DEEA),
    onSecondary = DeepWater,
    background = DeepWater,
    onBackground = Color(0xFFE8F4F8),
    surface = Color(0xFF124556),
    onSurface = Color(0xFFE8F4F8),
    surfaceVariant = Color(0xFF1A5A6E),
    onSurfaceVariant = Color(0xFFB2EBF2),
    error = AlertHigh,
    onError = Color.White,
    outline = Color(0xFF4DD0E1)
)
