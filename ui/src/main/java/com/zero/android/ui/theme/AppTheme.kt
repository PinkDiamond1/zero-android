package com.zero.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme =
	darkColorScheme(
		primary = Purple200,
		secondary = Purple700,
		tertiary = Teal200,
		background = Black,
		error = Red300,
		outline = LavenderIndigo,
		inversePrimary = Blue300
	)

private val LightColorScheme =
	lightColorScheme(
		primary = Purple500,
		secondary = Purple700,
		tertiary = Teal200,
		background = White,
		error = Red300,
		outline = LavenderIndigo,
		inversePrimary = Blue300
	)

@Composable
fun ZeroTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	randomThemePalette: Int = 0,
	content: @Composable () -> Unit
) {
	val colorsScheme = if (darkTheme) DarkColorScheme else LightColorScheme
	val extendedColors = getThemePalette(darkTheme, randomThemePalette)

	CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
		MaterialTheme(
			colorScheme = colorsScheme,
			typography = Typography,
			shapes = Shapes,
			content = content
		)
	}
}

object AppTheme {
	val colors: ExtendedColor
		@Composable get() = LocalExtendedColors.current
}
