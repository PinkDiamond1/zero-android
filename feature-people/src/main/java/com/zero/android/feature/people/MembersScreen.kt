package com.zero.android.feature.people

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zero.android.feature.people.ui.components.MemberListItem
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.Member
import com.zero.android.models.Network
import com.zero.android.models.fake.FakeModel
import com.zero.android.navigation.util.NavigationState
import com.zero.android.ui.extensions.Preview
import kotlinx.coroutines.flow.flowOf

@Composable
fun MembersRoute(
	network: Network?,
	viewModel: MembersViewModel = hiltViewModel(),
	onChannelCreated: (Channel) -> Unit
) {
	val navState by viewModel.navState.collectAsState()
	val loading by viewModel.loading.collectAsState()

	val pagedMembers = viewModel.members.collectAsLazyPagingItems()

	LaunchedEffect(network?.id) { network?.let { viewModel.onNetworkUpdated(it) } }

	LaunchedEffect(navState) {
		if (navState is NavigationState.Navigate) {
			onChannelCreated((navState as NavigationState.Navigate<DirectChannel>).data)
			viewModel.resetNavState()
		}
	}

	MembersScreen(
		members = pagedMembers,
		isRefreshing = loading,
		onRefresh = { viewModel.loadMembers() },
		onMemberSelected = { viewModel.onMembersSelected(it) }
	)
}

@Composable
fun MembersScreen(
	members: LazyPagingItems<Member>,
	isRefreshing: Boolean = false,
	onRefresh: () -> Unit,
	onMemberSelected: (Member) -> Unit
) {
	SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing), onRefresh = onRefresh) {
		LazyColumn {
			items(members) { member ->
				member ?: return@items
				MemberListItem(member, showStatus = true) { onMemberSelected(it) }
			}
		}
	}
}

@Preview
@Composable
private fun MembersScreenPreview() = Preview {
	MembersScreen(
		members = flowOf(PagingData.from(FakeModel.members())).collectAsLazyPagingItems(),
		onRefresh = {},
		onMemberSelected = {}
	)
}
