package com.zero.android.feature.channels.ui.details

import androidx.lifecycle.SavedStateHandle
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.ui.data
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.ChatMediaRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.feature.channels.navigation.ChannelDetailsDestination
import com.zero.android.models.Channel
import com.zero.android.models.GroupChannel
import com.zero.android.models.enums.AlertType
import com.zero.android.navigation.util.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelDetailsViewModel
@Inject
constructor(
	savedStateHandle: SavedStateHandle,
	private val channelRepository: ChannelRepository,
	private val chatMediaRepository: ChatMediaRepository,
	private val networkRepository: NetworkRepository
) : BaseViewModel() {

	val channelId: String = checkNotNull(savedStateHandle[ChannelDetailsDestination.ARG_CHANNEL_ID])
	val isGroupChannel: Boolean =
		checkNotNull(savedStateHandle[ChannelDetailsDestination.ARG_IS_GROUP_CHANNEL])

	private val _channel = MutableStateFlow<Result<Channel>>(Result.Loading)
	val channel = _channel.asStateFlow()
	val chatMedia = chatMediaRepository.chatMedia.map { it.take(20) }

	private val _navState = MutableStateFlow<NavigationState<Unit>?>(null)
	val navState = _navState.asStateFlow()

	init {
		loadChannel()
		getChatMedia()
	}

	private fun loadChannel() {
		ioScope.launch {
			val request =
				if (isGroupChannel) {
					channelRepository.getGroupChannel(channelId)
				} else {
					channelRepository.getDirectChannel(channelId)
				}

			request.asResult().collect(_channel)
		}
	}

	private fun getChatMedia() {
		ioScope.launch { chatMediaRepository.getChatMedia(channelId) }
	}

	fun updateAlerts(alertType: AlertType) {
		ioScope.launch {
			channel.data()?.let {
				if (isGroupChannel) {
					networkRepository.updateNotificationSettings(
						(it as GroupChannel).networkId,
						alertType = alertType
					)
				}
			}
		}
	}

	fun leaveChannel() {
		ioScope.launch {
			channel.data()?.let {
				channelRepository.leaveChannel(it)
				_navState.emit(NavigationState.Navigate(Unit))
			}
		}
	}
}
