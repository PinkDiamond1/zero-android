package com.zero.android.common.usecases

import kotlinx.coroutines.flow.StateFlow

interface SearchTriggerUseCase {
	val showSearchBar: StateFlow<Boolean>

	suspend fun triggerSearch(show: Boolean)
}
