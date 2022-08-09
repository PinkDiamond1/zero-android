package com.zero.android.feature.channels.ui.directchannels

data class DirectChannelScreenUiState(val directChannelsUiState: DirectChannelUiState)

sealed interface DirectChannelUiState {
	data class Success(val isSearchResult: Boolean = false) : DirectChannelUiState
	object Error : DirectChannelUiState
	object Loading : DirectChannelUiState
}
