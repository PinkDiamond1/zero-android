package com.zero.android.feature.messages.ui.mediaviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.MediaItem
import com.imherrera.videoplayer.VideoPlayer
import com.imherrera.videoplayer.VideoPlayerControl
import com.imherrera.videoplayer.rememberVideoPlayerState
import com.zero.android.common.R
import com.zero.android.common.extensions.showToast
import com.zero.android.models.ChatMedia
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.components.SmallClickableIcon
import com.zero.android.ui.components.ZoomableImage
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler

@Composable
fun MediaViewerRoute(onBackClick: () -> Unit, viewModel: ChatMediaViewModel = hiltViewModel()) {
	val context = LocalContext.current
	val chatMedia by viewModel.chatMedia.collectAsState(emptyList())
	val selectedMediaMessageId = viewModel.messageId

	LaunchedEffect(Unit) { viewModel.getChatMedia() }
	BackHandler { onBackClick() }

	if (chatMedia.isNotEmpty()) {
		MediaViewer(
			selectedMediaMessageId,
			chatMedia,
			onBackClick,
			downloadMedia = { media ->
				viewModel.downloadMedia(media)
				context.showToast(context.getString(R.string.downloading))
			}
		)
	}
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaViewer(
	selectedMediaMessageId: String,
	chatMedia: List<ChatMedia>,
	onBackClick: () -> Unit,
	downloadMedia: (ChatMedia) -> Unit
) {
	val filteredMedia = chatMedia.filter { !it.mediaUrl.isNullOrEmpty() }
	val initialPage = filteredMedia.indexOfFirst { it.messageId == selectedMediaMessageId }
	val pagerState = rememberPagerState(initialPage = if (initialPage > 0) initialPage else 0)
	val scrollEnabled = remember { mutableStateOf(true) }

	val topBar: @Composable () -> Unit = {
		AppBar(
			navIcon = {
				IconButton(onClick = onBackClick) {
					Icon(
						imageVector = Icons.Filled.ArrowBack,
						contentDescription = "Back",
						tint = AppTheme.colors.glow
					)
				}
			},
			color = AppTheme.colors.surfaceInverse,
			title = {},
			actions = {
				SmallClickableIcon(
					vector = Icons.Filled.Download,
					contentDescription = "Download Media",
					onClick = { filteredMedia[pagerState.currentPage].let(downloadMedia) }
				)
			}
		)
	}
	Scaffold(topBar = topBar) {
		Box(modifier = Modifier.padding(it)) {
			HorizontalPager(
				state = pagerState,
				count = filteredMedia.size,
				userScrollEnabled = scrollEnabled.value
			) { index ->
				Box {
					val media = filteredMedia[index]
					when (media.type) {
						MessageType.IMAGE -> {
							ZoomableImage(imageUrl = media.mediaUrl!!, scrollEnabled = scrollEnabled)
						}
						MessageType.VIDEO -> {
							MediaVideoPlayer(url = media.mediaUrl!!)
						}
						else -> {
							Text(
								text = "Un-supported media type",
								color = AppTheme.colors.colorTextPrimary,
								style = MaterialTheme.typography.displayLarge
							)
						}
					}
				}
			}
		}
	}
}

@Composable
fun MediaVideoPlayer(url: String) {
	val playerState = rememberVideoPlayerState()

	VideoPlayer(playerState = playerState) { VideoPlayerControl(state = playerState, title = "") }
	LaunchedEffect(Unit) {
		playerState.player.setMediaItem(MediaItem.fromUri(url))
		playerState.player.prepare()
		// playerState.player.playWhenReady = true
	}
}
