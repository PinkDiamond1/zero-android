package com.zero.android.ui.home

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zero.android.common.extensions.emitInScope
import com.zero.android.common.extensions.runOnMainThread
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.manager.SessionManager
import com.zero.android.data.repository.AuthRepository
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.InviteRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.models.Network
import com.zero.android.models.enums.AlertType
import com.zero.android.navigation.HomeDestination
import com.zero.android.navigation.NavDestination
import com.zero.android.ui.manager.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
	savedStateHandle: SavedStateHandle,
	private val preferences: Preferences,
	private val networkRepository: NetworkRepository,
	private val channelRepository: ChannelRepository,
	private val inviteRepository: InviteRepository,
	private val sessionManager: SessionManager,
	private val authRepository: AuthRepository,
	private val searchTriggerUseCase: SearchTriggerUseCase,
	private val themeManager: ThemeManager
) : BaseViewModel() {

	private val _inviteCode: String? = savedStateHandle[HomeDestination.ARG_INVITE_CODE]
	private var isInviteConsumed: Boolean = false
	val inviteCode: String?
		get() = if (isInviteConsumed) null else _inviteCode

	val isUserLoggedIn = MutableStateFlow<Boolean?>(null)
	val loggedInUserImage = runBlocking(Dispatchers.IO) { preferences.userImage() }

	val currentScreen = MutableStateFlow<NavDestination>(ChannelsDestination)

	private var allNetworks: List<Network>? = null
	val selectedNetwork = MutableStateFlow<Network?>(null)
	val networks = MutableStateFlow<Result<List<Network>>>(Result.Loading)

	val selectedNetworkSetting = MutableStateFlow<Network?>(null)

	val unreadDMsCount = MutableStateFlow(0)

	init {
		checkAuthOnLaunch()
	}

	private fun checkAuthOnLaunch() {
		val authCredentials = runBlocking(Dispatchers.IO) { preferences.userCredentials() }
		val isLoggedIn = authCredentials != null
		if (isLoggedIn) {
			checkInvite()
		}
		isUserLoggedIn.emitInScope(isLoggedIn)
	}

	private fun checkInvite() {
		ioScope.launch {
			_inviteCode?.let {
				inviteRepository.onInvite(it).asResult().collect { result ->
					if (result is Result.Success || result is Result.Error) {
						val inviteId: String? = (result as? Result.Success)?.data?.id
						initNetworks(inviteId)
					}
					isInviteConsumed = true
				}
			}
				?: initNetworks()
		}
	}

	private suspend fun initNetworks(inviteId: String? = null) {
		loadNetworks(inviteId)
		channelRepository.getUnreadDirectMessagesCount().map { unreadDMsCount.emit(it) }
	}

	private suspend fun loadNetworks(inviteCode: String? = null) {
		allNetworks = null
		networkRepository.getNetworks().asResult().collect { result ->
			if (result is Result.Loading) {
				if (allNetworks == null) networks.emit(result)
				return@collect
			}

			if (result is Result.Success && result.data.isNotEmpty()) {
				allNetworks = result.data
			}

			val setSelectedNetwork: (Network?) -> Unit = { network ->
				if (network != null) {
					onNetworkSelected(network)
				} else {
					allNetworks?.takeIf { it.isNotEmpty() }?.let { onNetworkSelected(it[0]) }
				}
			}
			if (inviteCode.isNullOrEmpty()) {
				setSelectedNetwork(selectedNetwork.firstOrNull())
			} else {
				inviteRepository.getInviteDetails(inviteCode).asResult().collect { inviteResult ->
					if (inviteResult is Result.Success) {
						val network =
							allNetworks?.firstOrNull { it.id == inviteResult.data.networkId }
								?: selectedNetwork.firstOrNull()
						setSelectedNetwork(network)
					} else {
						setSelectedNetwork(selectedNetwork.firstOrNull())
					}
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
		ioScope.launch {
			awaitAll(async { authRepository.revokeToken() }, async { sessionManager.logout(context) })
			runOnMainThread { onLogout() }
			themeManager.changeThemePalette(default = true)
		}
	}
}
