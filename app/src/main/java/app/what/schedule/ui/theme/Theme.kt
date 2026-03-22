package app.what.schedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import app.what.foundation.ui.theme.WHATTheme
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.data.local.settings.ThemeStyle
import app.what.schedule.data.local.settings.ThemeType
import app.what.schedule.data.local.settings.rememberAppValues
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF21A038),
    onPrimary = Color(0xFF05120A),
    primaryContainer = Color(0xFF10341D),
    onPrimaryContainer = Color(0xFFE3F8E8),
    secondary = Color(0xFF27C2A4),
    onSecondary = Color(0xFF071411),
    secondaryContainer = Color(0xFF113832),
    onSecondaryContainer = Color(0xFFD8F8F0),
    tertiary = Color(0xFF7ED957),
    onTertiary = Color(0xFF0A1506),
    background = Color(0xFF0F1210),
    onBackground = Color(0xFFF4F7F4),
    surface = Color(0xFF141916),
    onSurface = Color(0xFFF4F7F4),
    surfaceVariant = Color(0xFF202823),
    onSurfaceVariant = Color(0xFFB1BBB3),
    surfaceContainer = Color(0xFF171E19),
    surfaceContainerHigh = Color(0xFF1D2520),
    outline = Color(0xFF3D5344),
    outlineVariant = Color(0xFF29352D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF21A038),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF5E2),
    onPrimaryContainer = Color(0xFF08210D),
    secondary = Color(0xFF1AA88A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD8F4EE),
    onSecondaryContainer = Color(0xFF0A231C),
    tertiary = Color(0xFF6FCB4A),
    onTertiary = Color.White,
    background = Color(0xFFF4F8F4),
    onBackground = Color(0xFF171C18),
    surface = Color(0xFFFCFFFC),
    onSurface = Color(0xFF171C18),
    surfaceVariant = Color(0xFFDDE8DF),
    onSurfaceVariant = Color(0xFF4C5B50),
    surfaceContainer = Color(0xFFF0F6F1),
    surfaceContainerHigh = Color(0xFFE7F0E8),
    outline = Color(0xFF738477),
    outlineVariant = Color(0xFFC5D3C8),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun AppTheme(
    settings: AppValues = rememberAppValues(),
    content: @Composable () -> Unit
) {
    val themeType by settings.themeType.collect()
    val themeStyle by settings.themeStyle.collect()

    val isDarkTheme = when (themeType) {
        ThemeType.Dark -> true
        ThemeType.System -> isSystemInDarkTheme()
        else -> false
    }

    WHATTheme(
        theme = if (isDarkTheme) DarkScheme else LightScheme,
        dynamicColor = themeStyle == ThemeStyle.Material,
        isDarkTheme = isDarkTheme,
        content = content
    )
}
