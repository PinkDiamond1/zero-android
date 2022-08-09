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
import com.zero.android.models.ChannelCategory
import com.zero.android.models.GroupChannel
import com.zero.android.models.Network
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

	val pagers: MutableStateFlow<MutableMap<ChannelCategory, Flow<PagingData<GroupChannel>>>> =
		MutableStateFlow(mutableMapOf())
	val filteredPager = MutableStateFlow<PagingData<GroupChannel>>(PagingData.empty())

	val showSearchBar: StateFlow<Boolean> = searchTriggerUseCase.showSearchBar
	val categoriesState = MutableStateFlow<Result<List<ChannelCategory>>>(Result.Loading)
	val searchState = MutableStateFlow(false)

	fun onNetworkUpdated(network: Network) {
		this.network = network
		viewModelScope.launch { pagers.emit(mutableMapOf()) }
		loadCategories()
	}

	fun filterChannels(query: String) {
		ioScope.launch {
			searchState.emit(true)
			channelRepository
				.getGroupChannels(network.id, search = query)
				.cachedIn(viewModelScope)
				.collect { filteredPager.emit(it) }
		}
	}

	fun onSearchClosed() {
		ioScope.launch {
			filteredPager.emit(PagingData.empty())
			searchState.emit(false)
			searchTriggerUseCase.triggerSearch(false)
		}
	}

	private fun loadCategories() {
		ioScope.launch {
			networkRepository.getCategories(network.id).asResult().collect {
				if (it is Result.Success) {
					val categories = it.data.toMutableList()
					categories.add(0, "All") // TODO: use resource string

					createPagingData(categories)
					categoriesState.emit(Result.Success(categories))
				} else {
					categoriesState.emit(it)
				}
			}
		}
	}

	private fun getPagingData(category: ChannelCategory?) =
		channelRepository
			.getGroupChannels(network.id, category = if (category == "All") null else category)
			.cachedIn(viewModelScope)

	private fun createPagingData(categories: List<ChannelCategory>) {
		categories.forEach { category ->
			pagers.value.let { pagers ->
				if (pagers.containsKey(category)) return@forEach
				pagers[category] = getPagingData(category)
			}
		}
	}
}
