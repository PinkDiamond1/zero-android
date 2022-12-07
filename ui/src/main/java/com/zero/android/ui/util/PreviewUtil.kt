package com.zero.android.ui.util

import androidx.compose.runtime.Composable
import com.zero.android.ui.theme.ZeroTheme

@Composable
fun Preview(darkTheme: Boolean = true, content: @Composable () -> Unit) {
	ZeroTheme(darkTheme = darkTheme) { content() }
}
