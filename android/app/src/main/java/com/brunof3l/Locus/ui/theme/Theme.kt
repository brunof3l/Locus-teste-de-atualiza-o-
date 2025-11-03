package com.brunof3l.locus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
  primary = Color(0xFF0A2540),
  secondary = Color(0xFF007BFF),
  tertiary = Color(0xFF28A745),
  error = Color(0xFFE63946),
  background = Color(0xFFF8F9FA),
  surface = Color(0xFFFFFFFF),
  onPrimary = Color.White,
  onSecondary = Color.White,
  onBackground = Color(0xFF212529),
  onSurface = Color(0xFF212529),
  outline = Color(0xFFDEE2E6)
)

private val DarkColors = darkColorScheme(
  primary = Color(0xFFE6F0FF),
  secondary = Color(0xFF4DA3FF),
  tertiary = Color(0xFF28A745),
  error = Color(0xFFE63946),
  background = Color(0xFF0F1117),
  surface = Color(0xFF1A1D22),
  onPrimary = Color(0xFF0F1117),
  onSecondary = Color(0xFF0F1117),
  onBackground = Color(0xFFEAEAEA),
  onSurface = Color(0xFFEAEAEA),
  outline = Color(0xFF2A2F36)
)

private val LocusShapes = Shapes(
  extraSmall = RoundedCornerShape(8.dp),
  small = RoundedCornerShape(10.dp),
  medium = RoundedCornerShape(12.dp),
  large = RoundedCornerShape(16.dp)
)

@Composable
fun LocusTheme(useDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colors = if (useDarkTheme) DarkColors else LightColors
  MaterialTheme(
    colorScheme = colors,
    shapes = LocusShapes,
    typography = MaterialTheme.typography,
    content = content
  )
}