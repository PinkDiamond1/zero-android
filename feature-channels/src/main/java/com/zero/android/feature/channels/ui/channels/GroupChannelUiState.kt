package com.zero.android.feature.channels.ui.channels

import androidx.paging.PagingData
import androidx.paging.filter
import com.zero.android.feature.channels.model.ChannelTab
import com.zero.android.models.GroupChannel

data class GroupChannelUiState(
	val categoriesUiState: ChannelCategoriesUiState,
	val categoryChannelsUiState: CategoryChannelsUiState
) {
	private val allChannels =
		if (categoryChannelsUiState is CategoryChannelsUiState.Success) {
			categoryChannelsUiState.channels
		} else PagingData.empty()

	fun getChannels(category: String): PagingData<GroupChannel> {
		return if (category.equals("All", true)) allChannels
		else allChannels.filter { it.category == category }
	}
}

sealed interface ChannelCategoriesUiState {
	data class Success(val categories: List<ChannelTab>) : ChannelCategoriesUiState
	object Error : ChannelCategoriesUiState
	object Loading : ChannelCategoriesUiState
}

sealed interface CategoryChannelsUiState {
	data class Success(val channels: PagingData<GroupChannel>, val isSearchResult: Boolean = false) :
		CategoryChannelsUiState

	object Error : CategoryChannelsUiState
	object Loading : CategoryChannelsUiState
}
