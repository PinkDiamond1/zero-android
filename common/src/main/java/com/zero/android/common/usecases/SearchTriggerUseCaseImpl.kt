package com.zero.android.common.usecases

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SearchTriggerUseCaseImpl @Inject constructor() : SearchTriggerUseCase {

	private val _showSearchBar = MutableStateFlow(false)
	override val showSearchBar: StateFlow<Boolean> = _showSearchBar

	override suspend fun triggerSearch(show: Boolean) {
		_showSearchBar.emit(show)
	}
}
