package com.zero.android.feature.channels.ui.directchannels

import androidx.paging.PagingData
import com.zero.android.models.DirectChannel

data class DirectChannelScreenUiState(val directChannelsUiState: DirectChannelUiState)

sealed interface DirectChannelUiState {
	data class Success(val channels: PagingData<DirectChannel>, val isSearchResult: Boolean = false) :
		DirectChannelUiState
	object Error : DirectChannelUiState
	object Loading : DirectChannelUiState
}
