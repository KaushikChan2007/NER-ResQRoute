package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalThemeIsDark = compositionLocalOf { false }

private val ResQRouteDarkColorScheme = darkColorScheme(
  primary = EarthBrownPrimaryDark,
  onPrimary = EarthOnPrimaryDark,
  primaryContainer = EarthPrimaryContainerDark,
  onPrimaryContainer = EarthOnPrimaryContainerDark,
  secondary = EarthSecondaryDark,
  onSecondary = EarthOnSecondaryDark,
  secondaryContainer = EarthSecondaryContainerDark,
  onSecondaryContainer = EarthOnSecondaryContainerDark,
  tertiary = EarthTertiaryDark,
  onTertiary = EarthOnTertiaryDark,
  tertiaryContainer = EarthTertiaryContainerDark,
  onTertiaryContainer = EarthOnTertiaryContainerDark,
  background = EarthBackgroundDark,
  onBackground = EarthOnBackgroundDark,
  surface = EarthSurfaceDark,
  onSurface = EarthOnSurfaceDark,
  surfaceVariant = EarthSurfaceVariantDark,
  onSurfaceVariant = EarthOnSurfaceVariantDark,
  outline = EarthOutlineDark,
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005)
)

private val ResQRouteLightColorScheme = lightColorScheme(
  primary = EarthBrownPrimaryLight,
  onPrimary = EarthOnPrimaryLight,
  primaryContainer = EarthPrimaryContainerLight,
  onPrimaryContainer = EarthOnPrimaryContainerLight,
  secondary = EarthSecondaryLight,
  onSecondary = EarthOnSecondaryLight,
  secondaryContainer = EarthSecondaryContainerLight,
  onSecondaryContainer = EarthOnSecondaryContainerLight,
  tertiary = EarthTertiaryLight,
  onTertiary = EarthOnTertiaryLight,
  tertiaryContainer = EarthTertiaryContainerLight,
  onTertiaryContainer = EarthOnTertiaryContainerLight,
  background = EarthBackgroundLight,
  onBackground = EarthOnBackgroundLight,
  surface = EarthSurfaceLight,
  onSurface = EarthOnSurfaceLight,
  surfaceVariant = EarthSurfaceVariantLight,
  onSurfaceVariant = EarthOnSurfaceVariantLight,
  outline = EarthOutlineLight,
  error = RiskRedBlocked,
  onError = Color(0xFFFFFFFF)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) ResQRouteDarkColorScheme else ResQRouteLightColorScheme

  CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
