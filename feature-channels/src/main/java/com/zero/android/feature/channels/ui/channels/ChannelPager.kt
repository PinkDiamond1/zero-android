package com.zero.android.feature.channels.ui.channels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.zero.android.feature.channels.ui.components.ChannelListItem
import com.zero.android.models.Channel
import com.zero.android.models.ChannelCategory
import com.zero.android.models.GroupChannel
import com.zero.android.ui.components.InstantAnimation
import com.zero.android.ui.util.OnConnectionChanged
import com.zero.android.ui.util.OnResume
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ChannelPager(
	pagerState: PagerState,
	pagers: Map<ChannelCategory, Flow<PagingData<GroupChannel>>>,
	categories: List<ChannelCategory>,
	onClick: (Channel) -> Unit
) {
	InstantAnimation {
		HorizontalPager(state = pagerState, count = categories.size, userScrollEnabled = false) { index
			->
			Column(modifier = Modifier.fillMaxSize()) {
				pagers[categories[index]]?.let { PagedChannels(pagedData = it, onClick = onClick) }
			}
		}
	}
}

@Composable
private fun PagedChannels(pagedData: Flow<PagingData<GroupChannel>>, onClick: (Channel) -> Unit) {
	val items = pagedData.collectAsLazyPagingItems()

	OnResume { items.refresh() }
	OnConnectionChanged { if (it) items.refresh() }

	LazyColumn {
		items(items) { channel ->
			channel ?: return@items
			ChannelListItem(channel = channel, onClick = onClick)
		}
	}
}
