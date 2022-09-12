package com.zero.android.common.usecases

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ThemePaletteUseCaseImpl @Inject constructor() : ThemePaletteUseCase {

	private var lastThemePalette: Int = 0
	private val _dynamicThemePalette = MutableStateFlow(lastThemePalette)
	override val dynamicThemePalette: StateFlow<Int> = _dynamicThemePalette

	override suspend fun changeThemePalette(default: Boolean) {
		if (default) {
			lastThemePalette = 0
		} else {
			lastThemePalette++
			lastThemePalette %= 4
		}
		_dynamicThemePalette.emit(lastThemePalette)
	}
}
