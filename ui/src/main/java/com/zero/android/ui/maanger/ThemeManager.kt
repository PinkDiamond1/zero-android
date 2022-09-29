package com.zero.android.ui.maanger

import kotlinx.coroutines.flow.StateFlow

interface ThemeManager {
	val dynamicThemePalette: StateFlow<Int>

	suspend fun changeThemePalette(default: Boolean = false)
}
