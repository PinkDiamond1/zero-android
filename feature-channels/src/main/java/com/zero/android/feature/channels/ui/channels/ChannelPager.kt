package com.zero.android.feature.channels.ui.channels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.zero.android.feature.channels.ui.components.ChannelListItem
import com.zero.android.models.Channel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ChannelPager(pagerState: PagerState, ui: GroupChannelUiState, onClick: (Channel) -> Unit) {
	val categories = (ui.categoriesUiState as ChannelCategoriesUiState.Success).categories
	HorizontalPager(state = pagerState, count = categories.size) { index ->
		Column(modifier = Modifier.fillMaxSize()) {
            val channels = ui.getChannels(categories[index].name).collectAsLazyPagingItems()
			LazyColumn {
				items(channels) { channel ->
                    if (channel != null) {
                        ChannelListItem(channel = channel, onClick = onClick)
                    }
                }
			}
		}
	}
}
