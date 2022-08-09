package com.zero.android.feature.channels.ui.channels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.feature.channels.model.ChannelTab
import com.zero.android.models.ChannelCategory
import com.zero.android.models.GroupChannel
import com.zero.android.models.Network
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel
@Inject
constructor(
	private val networkRepository: NetworkRepository,
	private val channelRepository: ChannelRepository,
	private val searchTriggerUseCase: SearchTriggerUseCase
) : BaseViewModel() {

	private lateinit var network: Network
	private val _categories = MutableStateFlow<Result<List<ChannelCategory>>>(Result.Loading)
	private val _channels = MutableStateFlow<Result<PagingData<GroupChannel>>>(Result.Loading)
	private val _filteredChannels =
		MutableStateFlow<Result<PagingData<GroupChannel>?>>(Result.Success(null))

	val showSearchBar: StateFlow<Boolean> = searchTriggerUseCase.showSearchBar
	val uiState: StateFlow<GroupChannelUiState> =
		combine(_categories, _channels, _filteredChannels) {
				categoriesResult,
				channelsResult,
				filteredChannelResult ->
			if (categoriesResult is Result.Success && channelsResult is Result.Success) {
				val categories =
					mutableListOf<String>().apply {
						if (categoriesResult.data.isNotEmpty()) {
							add("All")
						}
						addAll(categoriesResult.data)
					}
				val channels = channelsResult.data

				val channelTabs = categories.map { ChannelTab(it.hashCode().toLong(), it) }
				GroupChannelUiState(
					ChannelCategoriesUiState.Success(channelTabs),
					if (filteredChannelResult is Result.Success &&
						filteredChannelResult.data != null
					) {
						CategoryChannelsUiState.Success(filteredChannelResult.data!!, true)
					} else {
						CategoryChannelsUiState.Success(channels)
					}
				)
			} else if (categoriesResult is Result.Loading) {
				GroupChannelUiState(ChannelCategoriesUiState.Loading, CategoryChannelsUiState.Loading)
			} else {
				GroupChannelUiState(ChannelCategoriesUiState.Error, CategoryChannelsUiState.Error)
			}
		}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(1_000),
				initialValue =
				GroupChannelUiState(
					ChannelCategoriesUiState.Loading,
					CategoryChannelsUiState.Loading
				)
			)

	fun onNetworkUpdated(network: Network) {
		this.network = network
		loadCategories()
		loadChannels()
	}

	fun filterChannels(query: String) = loadChannels(search = query)

	fun onSearchClosed() {
		ioScope.launch {
			_filteredChannels.emit(Result.Success(null))
			searchTriggerUseCase.triggerSearch(false)
		}
	}

	private fun loadCategories() {
		ioScope.launch {
			networkRepository.getCategories(network.id).asResult().collect { _categories.emit(it) }
		}
	}

	private fun loadChannels(search: String? = null) {
		ioScope.launch {
			channelRepository.getGroupChannels(network.id, search = search)
                .cachedIn(ioScope).asResult().collect {
				if (search.isNullOrEmpty()) _channels.emit(it) else _filteredChannels.emit(it)
			}
		}
	}
}
