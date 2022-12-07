package com.zero.android.feature.channels.ui.channels

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.zero.android.models.GroupChannel
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.Preview

@Composable
fun ChannelSearchResult(
	channelList: LazyPagingItems<GroupChannel>,
	onClick: (GroupChannel) -> Unit
) {
	val channels = channelList.itemSnapshotList.items // TODO: optimize & use LazyPagingItems directly
	val categorisedChannels =
		channels.groupBy { if (it.category.isNullOrEmpty()) "Other" else it.category }
	Column(modifier = Modifier.fillMaxSize()) {
		Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
			categorisedChannels.forEach { entry ->
				entry.key?.let { ChannelSearchItem(it, entry.value, onClick) }
			}
		}
	}
}

@Composable
fun ChannelSearchItem(
	header: String,
	channels: List<GroupChannel>,
	onClick: (GroupChannel) -> Unit
) {
	Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				header,
				color = AppTheme.colors.colorTextPrimary,
				style = MaterialTheme.typography.bodyMedium
			)
			Text(
				"${channels.size} found",
				color = AppTheme.colors.colorTextSecondary,
				style = MaterialTheme.typography.labelMedium
			)
		}
		Spacer(modifier = Modifier.size(8.dp))
		LazyColumn(modifier = Modifier.fillMaxWidth()) {
			items(
				channels,
				itemContent = { channel ->
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.fillMaxWidth().clickable { onClick(channel) }
					) {
						CircularInitialsImage(size = 32.dp, name = channel.name, url = channel.image)
						Spacer(modifier = Modifier.size(8.dp))
						Text(
							text = channel.name,
							color = AppTheme.colors.colorTextPrimary,
							style = MaterialTheme.typography.bodyMedium,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)

						channel.icon?.let { icon ->
							Spacer(modifier = Modifier.padding(8.dp))
							Image(
								painter = painterResource(icon),
								contentDescription = "",
								modifier = Modifier.wrapContentSize().align(Alignment.CenterVertically),
								contentScale = ContentScale.Fit
							)
							Spacer(modifier = Modifier.padding(4.dp))
						}
					}
					Spacer(modifier = Modifier.size(6.dp))
				}
			)
		}
	}
}

@Preview
@Composable
private fun ChannelSearchItemPreview() = Preview {
	ChannelSearchItem(
		header = "Search",
		channels = listOf(FakeModel.GroupChannel(), FakeModel.GroupChannel()),
		onClick = {}
	)
}
