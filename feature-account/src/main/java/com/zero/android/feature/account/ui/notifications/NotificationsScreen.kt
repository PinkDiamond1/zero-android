package com.zero.android.feature.account.ui.notifications

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zero.android.models.Notification
import com.zero.android.models.enums.NotificationCategory
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.extensions.BodyPadding
import com.zero.android.ui.util.OnResume
import com.zero.android.ui.util.Preview
import kotlinx.coroutines.flow.flowOf

@Composable
fun NotificationsRoute(
	onOpenChannel: (String) -> Unit,
	viewModel: NotificationsViewModel = hiltViewModel()
) {
	val loading by viewModel.loading.collectAsState()
	val pagedNotifications = viewModel.notifications.collectAsLazyPagingItems()

	OnResume { pagedNotifications.refresh() }

	NotificationsScreen(
		notifications = pagedNotifications,
		loading = loading,
		onOpenChannel = onOpenChannel
	)
}

@Composable
fun NotificationsScreen(
	notifications: LazyPagingItems<Notification>,
	loading: Boolean = false,
	onOpenChannel: (String) -> Unit
) {
	SwipeRefresh(
		state = rememberSwipeRefreshState(loading),
		onRefresh = { notifications.refresh() }
	) {
		LazyColumn(
			modifier = Modifier.fillMaxSize(),
			contentPadding = BodyPadding(vertical = 0.5f, horizontal = 0f)
		) {
			items(notifications) { notification ->
				notification ?: return@items
				if (notification.category != NotificationCategory.TASK) {
					NotificationListItem(notification) {
						if (it.channelId != null) onOpenChannel(it.channelId!!)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun NotificationsScreenPreview() = Preview {
	NotificationsScreen(
		notifications =
		flowOf(
			PagingData.from(
				listOf(
					FakeModel.Notification(),
					FakeModel.Notification(),
					FakeModel.Notification()
				)
			)
		)
			.collectAsLazyPagingItems(),
		onOpenChannel = {}
	)
}
