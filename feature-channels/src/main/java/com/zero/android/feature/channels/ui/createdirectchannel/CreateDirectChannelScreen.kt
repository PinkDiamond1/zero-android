package com.zero.android.feature.channels.ui.createdirectchannel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.feature.channels.ui.members.SelectMembersScreen
import com.zero.android.feature.channels.ui.members.SelectMembersUI
import com.zero.android.models.DirectChannel
import com.zero.android.navigation.util.NavigationState.Navigate

@Composable
fun CreateDirectChannelRoute(
	viewModel: CreateDirectChannelViewModel = hiltViewModel(),
	onChannelCreated: (DirectChannel) -> Unit,
	onBackClick: () -> Unit
) {
	val users by viewModel.users.collectAsState()
	val selectedUsers by viewModel.selectedUsers.collectAsState()

	val loading by viewModel.loading.collectAsState()
	val navState by viewModel.navState.collectAsState()

	val searchText by viewModel.textSearch.collectAsState()

	LaunchedEffect(navState) {
		if (navState is Navigate) {
			onChannelCreated((navState as Navigate<DirectChannel>).data)
		}
	}

	SelectMembersScreen(
		ui =
		SelectMembersUI(
			title = stringResource(R.string.new_chat),
			cta = stringResource(R.string.create)
		),
		searchText = searchText,
		members = users,
		selectedUsers = selectedUsers,
		loading = loading,
		onBackClick = onBackClick,
		onDone = { viewModel.onDone() },
		onMemberSelected = { viewModel.selectMember(it) },
		onMemberRemoved = { viewModel.removeMember(it) },
		onSearchTextChange = { viewModel.onSearchTextChanged(it) }
	)
}
