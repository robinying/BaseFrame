package com.robin.baseframe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.robin.baseframe.R

/** Material 3 theme backed by the same semantic colors used by XML screens. */
@Composable
fun BaseFrameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val primary = colorResource(R.color.colorPrimary)
    val onPrimary = colorResource(R.color.colorOnPrimary)
    val background = colorResource(R.color.colorBackground)
    val surface = colorResource(R.color.colorSurface)
    val surfaceVariant = colorResource(R.color.colorSurfaceVariant)
    val onSurface = colorResource(R.color.colorOnSurface)
    val onSurfaceVariant = colorResource(R.color.colorOnSurfaceVariant)
    val outline = colorResource(R.color.colorOutline)
    val error = colorResource(R.color.colorError)

    val colors = if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            background = background,
            onBackground = onSurface,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline,
            error = error
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            background = background,
            onBackground = onSurface,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline,
            error = error
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = BaseFrameTypography,
        content = content
    )
}

private val BaseFrameTypography = Typography(
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)
