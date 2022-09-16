package com.zero.android.feature.channels.ui.createdirectchannel

import androidx.lifecycle.viewModelScope
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.MemberRepository
import com.zero.android.models.DirectChannel
import com.zero.android.models.Member
import com.zero.android.navigation.util.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CreateDirectChannelViewModel
@Inject
constructor(
	private val memberRepository: MemberRepository,
	private val channelRepository: ChannelRepository
) : BaseViewModel() {

	private val _textSearch = MutableStateFlow("")

	private val _users = MutableStateFlow<List<Member>>(emptyList())
	val users = _users.asStateFlow()

	private val selectedUserList = mutableListOf<Member>()
	private val _selectedUsers = MutableStateFlow<List<Member>>(emptyList())
	val selectedUsers = _selectedUsers.asStateFlow()

	val loading = MutableStateFlow(false)

	private val _navState = MutableStateFlow<NavigationState<DirectChannel>?>(null)
	val navState = _navState.asStateFlow()

	init {
		viewModelScope.launch {
			_textSearch.asStateFlow().debounce(200).collectLatest { query ->
				val selectedIds = selectedUserList.map { it.id }
				val members =
					memberRepository.getMembers(query).first().filter { member ->
						!selectedIds.contains(member.id)
					}
				_users.emit(members.distinct())
			}
		}
	}

	fun onDone() {
		if (selectedUserList.size == 0) return

		ioScope.launch {
			loading.emit(true)
			try {
				val channel = channelRepository.createDirectChannel(selectedUserList)
				_navState.emit(NavigationState.Navigate(channel))
			} catch (e: Exception) {
				loading.emit(false)
			}
		}
	}

	fun onSearchTextChanged(text: String) {
		_textSearch.value = text
	}

	fun selectMember(member: Member) {
		selectedUserList
			.find { it.id == member.id }
			.let {
				if (it == null) {
					selectedUserList.add(member)
					viewModelScope.launch { _selectedUsers.emit(selectedUserList) }
				}
			}
	}

	fun removeMember(member: Member) {
		selectedUserList
			.find { it.id == member.id }
			?.let {
				selectedUserList.remove(it)
				viewModelScope.launch { _selectedUsers.emit(selectedUserList) }
			}
	}
}
