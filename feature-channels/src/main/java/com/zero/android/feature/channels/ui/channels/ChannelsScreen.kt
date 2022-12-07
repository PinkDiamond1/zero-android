package com.zero.android.feature.channels.ui.channels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.zero.android.common.R
import com.zero.android.common.ui.Result
import com.zero.android.feature.channels.model.ChannelTab
import com.zero.android.models.Channel
import com.zero.android.models.ChannelCategory
import com.zero.android.models.GroupChannel
import com.zero.android.models.Network
import com.zero.android.ui.components.FadeAnimation
import com.zero.android.ui.components.FadeExpandAnimation
import com.zero.android.ui.components.SearchView
import com.zero.android.ui.util.Preview
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagerApi::class)
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
	val pagerState = rememberPagerState(initialPage = 0)

	LaunchedEffect(network?.id) {
		network?.let { viewModel.onNetworkUpdated(it) }
		pagerState.scrollToPage(0)
	}

	ChannelsScreen(
		categoriesUiState = categoriesUiState,
		pagers = pagers,
		filteredChannels = lazyFilteredItems,
		showSearchBar = showSearch,
		isSearchState = isSearchState,
		pagerState = pagerState,
		onChannelSelected = onChannelSelected,
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
	pagerState: PagerState,
	onChannelSelected: (Channel) -> Unit,
	onChannelSearched: (String) -> Unit,
	onSearchClosed: () -> Unit
) {
	val coroutineScope = rememberCoroutineScope()
	var searchText: String by remember { mutableStateOf("") }

	if (categoriesUiState is Result.Success) {
		val categories = categoriesUiState.data
		val tabs = categories.map { ChannelTab(0, it, 0) }
		if (tabs.isNotEmpty()) {
			Column(modifier = Modifier.fillMaxWidth()) {
				FadeExpandAnimation(visible = showSearchBar) {
					SearchView(
						searchText = searchText,
						placeHolder = stringResource(R.string.search_channels),
						onValueChanged = {
							searchText = it
							onChannelSearched(it)
						},
						onSearchCancelled = { onSearchClosed() }
					)
				}
				if (isSearchState) {
					FadeAnimation(visible = isSearchState) {
						ChannelSearchResult(filteredChannels) {
							onSearchClosed()
							onChannelSelected(it)
						}
					}
				} else {
					ChannelTabLayout(pagerState = pagerState, coroutineScope = coroutineScope, tabs = tabs)
					ChannelPager(pagerState = pagerState, pagers = pagers, categories = categories) {
						onChannelSelected(it)
					}
				}
			}
		}
	}
}

@Preview @Composable
fun ChannelsScreenPreview() = Preview {}
