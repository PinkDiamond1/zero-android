package com.zero.android.feature.channels.ui.createdirectchannel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.feature.people.ui.components.MemberCircleItem
import com.zero.android.feature.people.ui.components.MemberSectionList
import com.zero.android.models.DirectChannel
import com.zero.android.models.Member
import com.zero.android.models.fake.FakeModel
import com.zero.android.navigation.util.NavigationState.Navigate
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.components.InstantAnimation
import com.zero.android.ui.components.SearchView
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme

@Composable
fun CreateDirectChannelRoute(
	viewModel: CreateDirectChannelViewModel = hiltViewModel(),
	onChannelCreated: (DirectChannel) -> Unit,
	onBack: () -> Unit
) {
	val users by viewModel.users.collectAsState()
	val selectedUsers by viewModel.selectedUsers.collectAsState()

	val loading by viewModel.loading.collectAsState()
	val navState by viewModel.navState.collectAsState()

	LaunchedEffect(navState) {
		if (navState is Navigate) {
			onChannelCreated((navState as Navigate<DirectChannel>).data)
		}
	}

	CreateDirectChannelScreen(
		members = users,
		selectedUsers = selectedUsers,
		loading = loading,
		onBackClick = onBack,
		onDone = { viewModel.onDone() },
		onMemberSelected = { viewModel.selectMember(it) },
		onMemberRemoved = { viewModel.removeMember(it) },
		onSearchTextChange = { viewModel.onSearchTextChanged(it) }
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDirectChannelScreen(
	members: List<Member>,
	selectedUsers: List<Member>,
	loading: Boolean,
	onBackClick: () -> Unit,
	onDone: () -> Unit,
	onMemberSelected: (Member) -> Unit,
	onMemberRemoved: (Member) -> Unit,
	onSearchTextChange: (String) -> Unit
) {
	val topBar: @Composable () -> Unit = {
		AppBar(
			navIcon = {
				IconButton(onClick = onBackClick) {
					Icon(
						imageVector = Icons.Filled.ArrowBack,
						contentDescription = "cd_back",
						tint = AppTheme.colors.glow
					)
				}
			},
			title = { stringResource(id = R.string.new_chat) },
			actions = {
				OutlinedButton(onClick = onDone) {
					Text(text = stringResource(R.string.create), color = AppTheme.colors.colorTextPrimary)
				}
			}
		)
	}

	Scaffold(topBar = { topBar() }) { innerPaddings ->
		Box(Modifier.padding(innerPaddings)) {
			Column(modifier = Modifier.fillMaxWidth()) {
				SearchView(
					placeHolder = stringResource(R.string.search_users),
					onValueChanged = { onSearchTextChange(it) }
				)
				if (selectedUsers.isNotEmpty()) {
					SelectedMembers(members = selectedUsers, onMemberRemoved = onMemberRemoved)
				}
				MemberSectionList(members, onMemberSelected)
			}
		}
	}
}

@Composable
private fun SelectedMembers(
	modifier: Modifier = Modifier,
	members: List<Member>,
	onMemberRemoved: (Member) -> Unit
) {
	InstantAnimation(modifier = modifier) {
		LazyRow(modifier = modifier) { items(members) { member -> MemberCircleItem(member = member) } }
	}
}

@Preview
@Composable
fun CreateDirectChannelScreenPreview() = Preview {
	CreateDirectChannelScreen(
		members = FakeModel.members(),
		selectedUsers = listOf(FakeModel.Member("one")),
		loading = false,
		onBackClick = {},
		onDone = {},
		onMemberSelected = {},
		onMemberRemoved = {},
		onSearchTextChange = {}
	)
}
