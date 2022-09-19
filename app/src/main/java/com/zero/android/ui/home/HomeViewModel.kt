package com.zero.android.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.common.usecases.ThemePaletteUseCase
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.manager.AuthManager
import com.zero.android.data.repository.AuthRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.models.Network
import com.zero.android.models.enums.AlertType
import com.zero.android.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
	private val preferences: Preferences,
	private val networkRepository: NetworkRepository,
	private val authManager: AuthManager,
	private val authRepository: AuthRepository,
	private val searchTriggerUseCase: SearchTriggerUseCase,
	private val themePaletteUseCase: ThemePaletteUseCase
) : BaseViewModel() {

	val loggedInUserImage = runBlocking(Dispatchers.IO) { preferences.userImage() }

	val currentScreen = MutableStateFlow<NavDestination>(ChannelsDestination)

	private var allNetworks: List<Network>? = null
	val selectedNetwork = MutableStateFlow<Network?>(null)
	val networks = MutableStateFlow<Result<List<Network>>>(Result.Loading)

	val selectedNetworkSetting = MutableStateFlow<Network?>(null)

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

	fun switchTheme() {
		viewModelScope.launch { themePaletteUseCase.changeThemePalette() }
	}

	fun onNetworkSelected(network: Network) {
		viewModelScope.launch {
			selectedNetwork.emit(network)
			allNetworks?.let { allNetworks ->
				networks.emit(Result.Success(allNetworks.filter { it.id != network.id }))
			}
		}
	}

	fun onNetworkSettingSelected(network: Network?) {
		viewModelScope.launch { selectedNetworkSetting.emit(network) }
	}

	fun updateNetworkNotificationSetting(network: Network, alertType: AlertType) {
		ioScope.launch {
			networkRepository.updateNotificationSettings(network.id, alertType = alertType)
			selectedNetworkSetting.emit(null)
		}
	}

	fun triggerSearch(show: Boolean) {
		ioScope.launch { searchTriggerUseCase.triggerSearch(show) }
	}

	fun logout(context: Context, onLogout: () -> Unit) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				awaitAll(async { authRepository.revokeToken() }, async { authManager.logout(context) })
				withContext(Dispatchers.Main) { onLogout() }
				themePaletteUseCase.changeThemePalette(default = true)
			}
		}
	}
}
