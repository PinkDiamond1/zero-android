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
	val textSearch = _textSearch.asStateFlow()

	private val _users = MutableStateFlow<List<Member>>(emptyList())
	val users = _users.asStateFlow()

	private val _selectedUsers = MutableStateFlow<List<Member>>(emptyList())
	val selectedUsers = _selectedUsers.asStateFlow()

	val loading = MutableStateFlow(false)

	private val _navState = MutableStateFlow<NavigationState<DirectChannel>?>(null)
	val navState = _navState.asStateFlow()

	init {
		viewModelScope.launch {
			_textSearch.asStateFlow().debounce(100).collectLatest { query ->
				ioScope.launch { filterMembers(memberRepository.getMembers(query).first()) }
			}
		}
	}

	fun onDone() {
		if (_selectedUsers.value.isEmpty()) return

		ioScope.launch {
			loading.emit(true)
			try {
				val channel = channelRepository.createDirectChannel(_selectedUsers.value)
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
		_textSearch.value = ""
		_selectedUsers.value
			.find { it.id == member.id }
			.let {
				if (it == null) {
					viewModelScope.launch {
						_selectedUsers.value = _selectedUsers.value.toMutableList().apply { add(member) }
					}
				}
			}
	}

	fun removeMember(member: Member) {
		_selectedUsers.value
			.find { it.id == member.id }
			?.let {
				viewModelScope.launch {
					_selectedUsers.value = _selectedUsers.value.toMutableList().apply { remove(it) }
				}
			}
	}

	private fun filterMembers(members: List<Member>) {
		val selectedIds = _selectedUsers.value.map { it.id }
		val mMembers = members.filter { member -> !selectedIds.contains(member.id) }
		viewModelScope.launch { _users.emit(mMembers.distinct()) }
	}
}
