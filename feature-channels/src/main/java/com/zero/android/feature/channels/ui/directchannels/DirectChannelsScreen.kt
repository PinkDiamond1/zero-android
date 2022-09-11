package com.zero.android.feature.channels.ui.directchannels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.zero.android.common.R
import com.zero.android.feature.channels.ui.components.ChannelListItem
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.ui.components.FadeAnimation
import com.zero.android.ui.components.FadeExpandAnimation
import com.zero.android.ui.components.InstantAnimation
import com.zero.android.ui.components.SearchView
import com.zero.android.ui.extensions.OnLifecycleEvent
import com.zero.android.ui.extensions.Preview

@Composable
fun DirectChannelsRoute(
	viewModel: DirectChannelsViewModel = hiltViewModel(),
	onChannelSelected: (Channel) -> Unit
) {
	val initialLoad = remember { mutableStateOf(false) }
	val uiState: DirectChannelScreenUiState by viewModel.uiState.collectAsState()
	val showSearch: Boolean by viewModel.showSearchBar.collectAsState()

	val pagedChannels = viewModel.channels.collectAsLazyPagingItems()

	OnLifecycleEvent { _, event ->
		if (event == Lifecycle.Event.ON_START) {
			pagedChannels.refresh()
		}
		initialLoad.value = true
	}

	DirectChannelsScreen(
		loggedInUser = viewModel.loggedInUserId,
		channels = pagedChannels,
		uiState = uiState,
		showSearchBar = showSearch,
		onChannelSelected = onChannelSelected,
		onChannelSearched = { viewModel.filterChannels(it) },
		onSearchClosed = viewModel::onSearchClosed
	)
}

@Composable
fun DirectChannelsScreen(
	loggedInUser: String,
	channels: LazyPagingItems<DirectChannel>,
	uiState: DirectChannelScreenUiState,
	showSearchBar: Boolean = false,
	onChannelSelected: (Channel) -> Unit,
	onChannelSearched: (String) -> Unit,
	onSearchClosed: () -> Unit
) {
	val directChannelsUiState = uiState.directChannelsUiState as? DirectChannelUiState.Success

	Column(modifier = Modifier.fillMaxWidth()) {
		FadeExpandAnimation(visible = showSearchBar) {
			SearchView(
				placeHolder = stringResource(R.string.search_channels),
				onValueChanged = { onChannelSearched(it) },
				onSearchCancelled = { onSearchClosed() }
			)
		}
		if (directChannelsUiState != null) {
			DirectChannelsList(channels, loggedInUser, onChannelSelected)
		}
		SearchResultCount(
			show = directChannelsUiState?.isSearchResult == true,
			itemCount = channels.itemCount
		)
	}
}

@Composable
fun SearchResultCount(show: Boolean, itemCount: Int) {
	FadeAnimation(visible = show) {
		Text(
			text = "$itemCount results found",
			modifier =
			Modifier.fillMaxWidth()
				.padding(vertical = 10.dp)
				.background(MaterialTheme.colorScheme.primary),
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.labelMedium
		)
	}
}

@Composable
fun ColumnScope.DirectChannelsList(
	channels: LazyPagingItems<DirectChannel>,
	loggedInUser: String,
	onChannelSelected: (Channel) -> Unit
) {
	val modifier = Modifier.weight(1f)
	InstantAnimation(modifier = modifier) {
		LazyColumn(modifier = modifier) {
			items(channels) { channel ->
				channel ?: return@items
				ChannelListItem(loggedInUser, channel) { onChannelSelected(it) }
			}
		}
	}
}

@Preview @Composable
fun DirectChannelsScreenPreview() = Preview {}
