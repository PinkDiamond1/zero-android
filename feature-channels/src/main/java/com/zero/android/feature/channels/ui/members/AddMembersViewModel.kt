package com.zero.android.feature.channels.ui.members

import androidx.lifecycle.SavedStateHandle
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.data
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.MemberRepository
import com.zero.android.feature.channels.navigation.AddMembersDestination
import com.zero.android.models.Channel
import com.zero.android.models.Member
import com.zero.android.navigation.util.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMembersViewModel
@Inject
constructor(
	savedStateHandle: SavedStateHandle,
	memberRepository: MemberRepository,
	private val channelRepository: ChannelRepository
) : SelectMembersViewModel(memberRepository) {

	private val channelId: String =
		checkNotNull(savedStateHandle[AddMembersDestination.ARG_CHANNEL_ID])

	private val _channel = MutableStateFlow<Result<Channel>>(Result.Loading)

	init {
		loadChannel()
	}

	private fun loadChannel() {
		ioScope.launch { channelRepository.getChannel(channelId).asResult().collect(_channel) }
	}

	override fun onDone() {
		ioScope.launch {
			_channel.data()?.let { channel ->
				loading.emit(true)
				try {
					channelRepository.addMembers(channel.id, selectedUsers.value)
					_navState.emit(NavigationState.Navigate(channel))
				} catch (e: Exception) {
					loading.emit(false)
				}
			}
		}
	}

	override suspend fun filterMember(member: Member) =
		_channel.data()?.members?.find { it.id == member.id } != null
}
