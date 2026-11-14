package com.olimposystems.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Azul do logotipo MSX.
private val MsxBlue = Color(0xFF29549F)
private val MsxBlueLight = Color(0xFFADC6FF)

private val Claro = lightColorScheme(
    primary = MsxBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E2FF),
    onPrimaryContainer = Color(0xFF001A43),
)

private val Escuro = darkColorScheme(
    primary = MsxBlueLight,
    onPrimary = Color(0xFF002E6A),
    primaryContainer = Color(0xFF0A4495),
    onPrimaryContainer = Color(0xFFD8E2FF),
)

/** Material 3; usa as cores do papel de parede (Material You) no Android 12+. */
@Composable
fun MsxTheme(
    escuro: Boolean = isSystemInDarkTheme(),
    corDinamica: Boolean = true,
    content: @Composable () -> Unit,
) {
    val cores = when {
        corDinamica && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (escuro) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        escuro -> Escuro
        else -> Claro
    }
    MaterialTheme(colorScheme = cores, content = content)
}
