package com.zero.android.feature.channels.ui.directchannels

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zero.android.common.system.NetworkManager
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.common.util.ConnectionState
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.models.DirectChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DirectChannelsViewModel
@Inject
constructor(
	@ApplicationContext context: Context,
	private val preferences: Preferences,
	private val channelRepository: ChannelRepository,
	private val searchTriggerUseCase: SearchTriggerUseCase
) : BaseViewModel() {

	val loggedInUserId
		get() = runBlocking(Dispatchers.IO) { preferences.userId() }

	private val _uiState = MutableStateFlow(DirectChannelScreenUiState(DirectChannelUiState.Loading))
	val uiState: StateFlow<DirectChannelScreenUiState> = _uiState
	val showSearchBar: StateFlow<Boolean> = searchTriggerUseCase.showSearchBar

	private var channelsJob: Job? = null
	val channels: MutableStateFlow<PagingData<DirectChannel>> = MutableStateFlow(PagingData.empty())

	init {
		loadChannels()

		viewModelScope.launch {
			NetworkManager.observeConnection(context).collect {
				if (it is ConnectionState.Available && !showSearchBar.value) {
					loadChannels()
				}
			}
		}
	}

	fun filterChannels(query: String?) = loadChannels(search = query)

	fun onSearchClosed() {
		loadChannels()
		ioScope.launch { searchTriggerUseCase.triggerSearch(false) }
	}

	private fun loadChannels(search: String? = null) {
		channelsJob?.cancel()
		channelsJob =
			ioScope.launch {
				channelRepository
					.getDirectChannels(search = search)
					.cachedIn(viewModelScope)
					.asResult()
					.collect {
						when (it) {
							is Result.Success -> {
								channels.emit(it.data)
								_uiState.emit(
									DirectChannelScreenUiState(
										DirectChannelUiState.Success(isSearchResult = !search.isNullOrEmpty())
									)
								)
							}
							is Result.Loading ->
								_uiState.emit(DirectChannelScreenUiState(DirectChannelUiState.Loading))
							else -> _uiState.emit(DirectChannelScreenUiState(DirectChannelUiState.Error))
						}
					}
			}
	}
}
