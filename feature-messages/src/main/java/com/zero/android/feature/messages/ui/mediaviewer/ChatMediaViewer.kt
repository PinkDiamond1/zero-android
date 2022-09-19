package com.zero.android.feature.messages.ui.mediaviewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.MediaItem
import com.imherrera.videoplayer.VideoPlayer
import com.imherrera.videoplayer.VideoPlayerControl
import com.imherrera.videoplayer.rememberVideoPlayerState
import com.zero.android.models.ChatMedia
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

@Composable
fun MediaViewerRoute(onBackClick: () -> Unit, viewModel: ChatMediaViewModel = hiltViewModel()) {
	val chatMedia by viewModel.chatMedia.collectAsState(emptyList())
	val selectedMediaMessageId = viewModel.messageId

	LaunchedEffect(Unit) { viewModel.getChatMedia() }
	BackHandler { onBackClick() }

	if (chatMedia.isNotEmpty()) {
		MediaViewer(
			selectedMediaMessageId,
			chatMedia,
			onBackClick,
			downloadMedia = { media -> viewModel.downloadMedia(media) }
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
				IconButton(onClick = { filteredMedia[pagerState.currentPage].let(downloadMedia) }) {
					Icon(
						imageVector = Icons.Filled.Download,
						contentDescription = "Download Media",
						tint = AppTheme.colors.surface
					)
				}
			}
		)
	}
	Scaffold(topBar = topBar) {
		Box(modifier = Modifier.padding(it)) {
			HorizontalPager(state = pagerState, count = filteredMedia.size) { index ->
				Box {
					val media = filteredMedia[index]
					when (media.mediaType) {
						MessageType.IMAGE -> {
							ZoomableImage(imageUrl = media.mediaUrl!!)
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
		playerState.player.playWhenReady = true
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomableImage(
	modifier: Modifier = Modifier,
	imageUrl: String,
	maxScale: Float = 1f,
	minScale: Float = 3f,
	contentScale: ContentScale = ContentScale.Fit,
	isRotation: Boolean = false,
	isZoomable: Boolean = true
) {
	val scale = remember { mutableStateOf(1f) }
	val rotationState = remember { mutableStateOf(1f) }
	val offsetX = remember { mutableStateOf(1f) }
	val offsetY = remember { mutableStateOf(1f) }
	val lazyState = rememberLazyListState()

	val coroutineScope = rememberCoroutineScope()
	Box(
		modifier =
		Modifier.clip(RectangleShape)
			.combinedClickable(
				interactionSource = remember { MutableInteractionSource() },
				indication = null,
				onClick = {},
				onDoubleClick = {
					if (scale.value >= 2f) {
						scale.value = 1f
						offsetX.value = 1f
						offsetY.value = 1f
					} else scale.value = 3f
				}
			)
			.pointerInput(Unit) {
				if (isZoomable) {
					forEachGesture {
						awaitPointerEventScope {
							awaitFirstDown()
							do {
								val event = awaitPointerEvent()
								scale.value *= event.calculateZoom()
								if (scale.value > 1) {
									coroutineScope.launch { lazyState.setScrolling(false) }
									val offset = event.calculatePan()
									offsetX.value += offset.x
									offsetY.value += offset.y
									rotationState.value += event.calculateRotation()
									coroutineScope.launch { lazyState.setScrolling(true) }
								} else {
									scale.value = 1f
									offsetX.value = 1f
									offsetY.value = 1f
								}
							} while (event.changes.any { it.pressed })
						}
					}
				}
			}
	) {
		AsyncImage(
			model = imageUrl,
			contentDescription = null,
			contentScale = contentScale,
			modifier =
			modifier.fillMaxSize().align(Alignment.Center).graphicsLayer {
				if (isZoomable) {
					scaleX = maxOf(maxScale, minOf(minScale, scale.value))
					scaleY = maxOf(maxScale, minOf(minScale, scale.value))
					if (isRotation) {
						rotationZ = rotationState.value
					}
					translationX = offsetX.value
					translationY = offsetY.value
				}
			}
		)
	}
}

suspend fun LazyListState.setScrolling(value: Boolean) {
	scroll(scrollPriority = MutatePriority.PreventUserInput) {
		when (value) {
			true -> Unit
			else -> awaitCancellation()
		}
	}
}
