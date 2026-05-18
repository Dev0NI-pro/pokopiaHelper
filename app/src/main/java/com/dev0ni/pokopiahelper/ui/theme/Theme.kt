package com.dev0ni.pokopiahelper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = PokopiaTeal,
    onPrimary = PokopiaDarkBg,
    primaryContainer = PokopiaDarkCard,
    secondary = PokopiaGold,
    background = PokopiaDarkBg,
    surface = PokopiaDarkSurface,
    onSurface = PokopiaOnDark,
    onBackground = PokopiaOnDark
)

private val LightColors = lightColorScheme(
    primary = PokopiaTeal,
    onPrimary = PokopiaCardBg,
    primaryContainer = Color(0xFFB2F0EC),
    secondary = PokopiaGold,
    background = PokopiaLightBg,
    surface = PokopiaCardBg,
    onSurface = PokopiaNavy,
    onBackground = PokopiaNavy
)

@Composable
fun PokopiaHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
