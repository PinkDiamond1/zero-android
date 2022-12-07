package com.zero.android.feature.channels.ui.members

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.navigation.util.NavigationState

@Composable
fun AddMembersRoute(
	viewModel: AddMembersViewModel = hiltViewModel(),
	onDone: () -> Unit,
	onBackClick: () -> Unit
) {
	val users by viewModel.users.collectAsState()
	val selectedUsers by viewModel.selectedUsers.collectAsState()

	val loading by viewModel.loading.collectAsState()
	val navState by viewModel.navState.collectAsState()

	val searchText by viewModel.textSearch.collectAsState()

	LaunchedEffect(navState) { if (navState is NavigationState.Navigate) onDone() }

	SelectMembersScreen(
		ui =
		SelectMembersUI(
			title = stringResource(R.string.add_members),
			cta = stringResource(R.string.add)
		),
		searchText = searchText,
		members = users,
		selectedUsers = selectedUsers,
		loading = loading,
		onBackClick = onBackClick,
		onDone = { viewModel.onDoneClick() },
		onMemberSelected = { viewModel.selectMember(it) },
		onMemberRemoved = { viewModel.removeMember(it) },
		onSearchTextChange = { viewModel.onSearchTextChanged(it) }
	)
}
