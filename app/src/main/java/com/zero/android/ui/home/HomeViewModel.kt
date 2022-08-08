package com.zero.android.ui.home

import androidx.lifecycle.viewModelScope
import com.zero.android.common.navigation.NavDestination
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.data.repository.UserRepository
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.models.Network
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
	private val networkRepository: NetworkRepository,
	private val userRepository: UserRepository,
	private val searchTriggerUseCase: SearchTriggerUseCase
) : BaseViewModel() {

	val currentScreen = MutableStateFlow<NavDestination>(ChannelsDestination)

	private var allNetworks: List<Network>? = null
	val selectedNetwork = MutableStateFlow<Network?>(null)
	val networks = MutableStateFlow<Result<List<Network>>>(Result.Loading)

	init {
		loadNetworks()
	}

	private fun loadNetworks() {
		ioScope.launch {
			allNetworks = null
			networkRepository.getNetworks().asResult().collect { result ->
				if (result is Result.Loading) {
					if (allNetworks == null) networks.emit(result)
					return@collect
				}

				if (result is Result.Success && result.data.isNotEmpty()) {
					allNetworks = result.data
				}

				val selected = selectedNetwork.firstOrNull()
				if (selected == null) {
					allNetworks?.takeIf { it.isNotEmpty() }?.let { onNetworkSelected(it[0]) }
				} else {
					onNetworkSelected(selected)
				}
			}
		}
	}

	fun onNetworkSelected(network: Network) {
		viewModelScope.launch {
			selectedNetwork.emit(network)
			allNetworks?.let { allNetworks ->
				networks.emit(Result.Success(allNetworks.filter { it.id != network.id }))
			}
		}
	}

	fun triggerSearch(show: Boolean) {
		ioScope.launch { searchTriggerUseCase.triggerSearch(show) }
	}

	fun logout(onLogout: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			userRepository.logout()
			withContext(Dispatchers.Main) { onLogout() }
		}
	}
}
