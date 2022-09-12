package com.zero.android.common.usecases

import kotlinx.coroutines.flow.StateFlow

interface ThemePaletteUseCase {
	val dynamicThemePalette: StateFlow<Int>

	suspend fun changeThemePalette(default: Boolean = false)
}
