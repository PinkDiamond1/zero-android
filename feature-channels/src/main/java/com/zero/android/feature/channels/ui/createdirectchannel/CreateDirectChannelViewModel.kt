package com.zero.android.feature.channels.ui.createdirectchannel

import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.MemberRepository
import com.zero.android.feature.channels.ui.members.SelectMembersViewModel
import com.zero.android.navigation.util.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDirectChannelViewModel
@Inject
constructor(memberRepository: MemberRepository, private val channelRepository: ChannelRepository) :
	SelectMembersViewModel(memberRepository) {

	override fun onDone() {
		ioScope.launch {
			loading.emit(true)
			try {
				val channel = channelRepository.createDirectChannel(selectedUsers.value)
				_navState.emit(NavigationState.Navigate(channel))
			} catch (e: Exception) {
				loading.emit(false)
			}
		}
	}
}
