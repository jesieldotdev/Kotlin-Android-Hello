package com.example.myapplication.ui.theme // Seu pacote

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de cores para o modo claro (usando suas cores personalizadas)
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundEnd, // Usando sua cor de fundo
    surface = TodoCardBackground, // Usando cor de card
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary, // Usando sua cor de texto primário
    onSurface = TextPrimary     // Usando sua cor de texto primário
    /* Outras cores padrão podem ser sobrescritas aqui também */
)

// Paleta de cores para o modo escuro (usando suas cores personalizadas)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackgroundEnd, // Usando sua cor de fundo escura
    surface = DarkTodoCardBackground, // Usando cor de card escura
    onPrimary = PurpleGrey40, // Ajuste conforme necessário
    onSecondary = Pink40,     // Ajuste conforme necessário
    onTertiary = Purple40,    // Ajuste conforme necessário
    onBackground = DarkTextPrimary, // Usando sua cor de texto primário escura
    onSurface = DarkTextPrimary     // Usando sua cor de texto primário escura
)

@Composable
fun MyCustomTheme( // Renomeie se o nome do seu tema for diferente
    darkTheme: Boolean = isSystemInDarkTheme(), // Você vai controlar isso externamente
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Cor da barra de status
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Você já deve ter isso
        content = content
    )
}
