package com.zero.android.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.manager.SessionManager
import com.zero.android.data.repository.AuthRepository
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.models.Network
import com.zero.android.models.enums.AlertType
import com.zero.android.navigation.NavDestination
import com.zero.android.ui.manager.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
	private val preferences: Preferences,
	private val networkRepository: NetworkRepository,
	private val channelRepository: ChannelRepository,
	private val sessionManager: SessionManager,
	private val authRepository: AuthRepository,
	private val searchTriggerUseCase: SearchTriggerUseCase,
	private val themeManager: ThemeManager
) : BaseViewModel() {

	val loggedInUserImage = runBlocking(Dispatchers.IO) { preferences.userImage() }

	val currentScreen = MutableStateFlow<NavDestination>(ChannelsDestination)

	private var allNetworks: List<Network>? = null
	val selectedNetwork = MutableStateFlow<Network?>(null)
	val networks = MutableStateFlow<Result<List<Network>>>(Result.Loading)

	val selectedNetworkSetting = MutableStateFlow<Network?>(null)

	val unreadDMsCount = MutableStateFlow(0)

	init {
		loadNetworks()

		ioScope.launch {
			channelRepository.getUnreadDirectMessagesCount().map { unreadDMsCount.emit(it) }
		}
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
		viewModelScope.launch { themeManager.changeThemePalette() }
	}

	fun onNetworkSelected(network: Network) {
		viewModelScope.launch {
			selectedNetwork.emit(network)
			allNetworks?.let { allNetworks -> networks.emit(Result.Success(allNetworks)) }
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
				awaitAll(async { authRepository.revokeToken() }, async { sessionManager.logout(context) })
				withContext(Dispatchers.Main) { onLogout() }
				themeManager.changeThemePalette(default = true)
			}
		}
	}
}
