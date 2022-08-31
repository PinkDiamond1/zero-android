package com.zero.android.feature.channels.ui.channels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.zero.android.common.R
import com.zero.android.common.ui.Result
import com.zero.android.feature.channels.model.ChannelTab
import com.zero.android.models.Channel
import com.zero.android.models.ChannelCategory
import com.zero.android.models.GroupChannel
import com.zero.android.models.Network
import com.zero.android.ui.components.FadeExpandAnimation
import com.zero.android.ui.components.SearchView
import com.zero.android.ui.extensions.Preview
import kotlinx.coroutines.flow.Flow

@Composable
fun ChannelsRoute(
	network: Network?,
	viewModel: ChannelsViewModel = hiltViewModel(),
	onChannelSelected: (Channel) -> Unit
) {
	val showSearch: Boolean by viewModel.showSearchBar.collectAsState()

	val categoriesUiState by viewModel.categoriesState.collectAsState()
	val lazyFilteredItems = viewModel.filteredPager.collectAsLazyPagingItems()
	val isSearchState by viewModel.searchState.collectAsState()

	val pagers by viewModel.pagers.collectAsState()

	LaunchedEffect(network?.id) { network?.let { viewModel.onNetworkUpdated(it) } }
	ChannelsScreen(
		categoriesUiState,
		pagers,
		lazyFilteredItems,
		showSearch,
		isSearchState,
		onChannelSelected,
		onChannelSearched = { viewModel.filterChannels(it) },
		onSearchClosed = viewModel::onSearchClosed
	)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ChannelsScreen(
	categoriesUiState: Result<List<ChannelCategory>>,
	pagers: Map<ChannelCategory, Flow<PagingData<GroupChannel>>>,
	filteredChannels: LazyPagingItems<GroupChannel>,
	showSearchBar: Boolean = false,
	isSearchState: Boolean = false,
	onChannelSelected: (Channel) -> Unit,
	onChannelSearched: (String) -> Unit,
	onSearchClosed: () -> Unit
) {
	val coroutineScope = rememberCoroutineScope()
	val pagerState = rememberPagerState(initialPage = 0)

	if (categoriesUiState is Result.Success) {
		val categories = categoriesUiState.data
		val tabs = categories.map { ChannelTab(0, it, 0) }
		if (tabs.isNotEmpty()) {
			Column(modifier = Modifier.fillMaxWidth()) {
				FadeExpandAnimation(visible = showSearchBar) {
					SearchView(
						placeHolder = stringResource(R.string.search_channels),
						onValueChanged = { onChannelSearched(it) },
						onSearchCancelled = { onSearchClosed() }
					)
				}
				FadeExpandAnimation(visible = isSearchState) {
					ChannelSearchResult(filteredChannels) {
						onSearchClosed()
						onChannelSelected(it)
					}
				}
				ChannelTabLayout(pagerState = pagerState, coroutineScope = coroutineScope, tabs = tabs)
				ChannelPager(pagerState = pagerState, pagers = pagers, categories = categories) {
					onChannelSelected(it)
				}
			}
		}
	}
}

@Preview @Composable
fun ChannelsScreenPreview() = Preview {}
