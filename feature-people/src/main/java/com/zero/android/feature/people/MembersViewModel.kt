package com.zero.android.feature.people

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zero.android.common.system.Logger
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.MemberRepository
import com.zero.android.models.DirectChannel
import com.zero.android.models.Member
import com.zero.android.models.Network
import com.zero.android.navigation.util.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersViewModel
@Inject
constructor(
	private val memberRepository: MemberRepository,
	private val channelRepository: ChannelRepository,
	private val logger: Logger
) : BaseViewModel() {

	private lateinit var network: Network
	private var membersJob: Job? = null

	private val _members = MutableStateFlow<PagingData<Member>>(PagingData.empty())
	val members = _members.asStateFlow()

	private val _navState = MutableStateFlow<NavigationState<DirectChannel>?>(null)
	val navState = _navState.asStateFlow()

	private val _loading = MutableStateFlow(false)
	val loading = _loading.asStateFlow()

	fun onNetworkUpdated(network: Network) {
		this.network = network
		loadMembers()
	}

	private fun loadMembers() {
		_loading.value = true

		membersJob?.cancel()
		membersJob =
			ioScope.launch {
				memberRepository
					.getByNetwork(network.id)
					.cachedIn(viewModelScope)
					.onEach { _loading.emit(false) }
					.collect(_members)
			}
	}

	fun onMembersSelected(member: Member) {
		ioScope.launch {
			try {
				val channel = channelRepository.createDirectChannel(listOf(member))
				_navState.emit(NavigationState.Navigate(channel))
			} catch (e: Exception) {
				logger.e("Failed to create direct channel for member", e)
			}
		}
	}

	fun resetNavState() = viewModelScope.launch { _navState.emit(NavigationState.Blank) }
}
