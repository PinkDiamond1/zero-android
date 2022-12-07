package com.zero.android.feature.channels.ui.members

import androidx.lifecycle.viewModelScope
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.repository.MemberRepository
import com.zero.android.models.Channel
import com.zero.android.models.Member
import com.zero.android.navigation.util.NavigationState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
abstract class SelectMembersViewModel constructor(private val memberRepository: MemberRepository) :
	BaseViewModel() {

	private val _textSearch = MutableStateFlow("")
	val textSearch = _textSearch.asStateFlow()

	private val _users = MutableStateFlow<List<Member>>(emptyList())
	val users = _users.asStateFlow()

	private val _selectedUsers = MutableStateFlow<List<Member>>(emptyList())
	val selectedUsers = _selectedUsers.asStateFlow()

	val loading = MutableStateFlow(false)

	protected val _navState = MutableStateFlow<NavigationState<Channel>?>(null)
	val navState = _navState.asStateFlow()

	init {
		viewModelScope.launch {
			_textSearch.asStateFlow().debounce(100).collectLatest { query ->
				ioScope.launch { searchMembers(memberRepository.getMembers(query).first()) }
			}
		}
	}

	fun onDoneClick() {
		if (_selectedUsers.value.isEmpty()) return
		onDone()
	}

	abstract fun onDone()

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

	private suspend fun searchMembers(members: List<Member>) {
		val selectedIds = _selectedUsers.value.map { it.id }
		val mMembers =
			members.filter { member -> !selectedIds.contains(member.id) && !filterMember(member) }
		viewModelScope.launch { _users.emit(mMembers.distinct()) }
	}

	protected open suspend fun filterMember(member: Member) = false
}
