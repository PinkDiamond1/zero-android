package com.zero.android.feature.channels.ui.directchannels

data class DirectChannelScreenUIState(val directChannelsUiState: DirectChannelUIState)

sealed interface DirectChannelUIState {
	data class Success(val isSearchResult: Boolean = false) : DirectChannelUIState
	object Error : DirectChannelUIState
	object Loading : DirectChannelUIState
}
