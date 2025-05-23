package com.example.labworks.ui.theme

import android.app.Activity
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

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5), // Tamsesnis mėlynas (vietoj Purple80)
    secondary = Color(0xFF546E7A), // Tamsus pilkai-melsvas (vietoj PurpleGrey80)
    tertiary = Color(0xFF8E24AA) // Tamsesnis rožinis/violetinis (vietoj Pink80)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3), // Šviesesnis mėlynas (vietoj Purple40)
    secondary = Color(0xFF90A4AE), // Šviesus pilkai-melsvas (vietoj PurpleGrey40)
    tertiary = Color(0xFFE91E63) // Šviesus rožinis (vietoj Pink40)
)


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun LabWorksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}