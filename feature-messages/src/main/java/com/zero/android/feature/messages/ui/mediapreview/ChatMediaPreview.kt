package com.zero.android.feature.messages.ui.mediapreview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.android.exoplayer2.MediaItem
import com.imherrera.videoplayer.VideoPlayer
import com.imherrera.videoplayer.VideoPlayerControl
import com.imherrera.videoplayer.rememberVideoPlayerState
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.theme.AppTheme
import java.io.File

@Composable
fun MediaPreview(mediaFile: File, type: MessageType, onBack: () -> Unit, sendMedia: () -> Unit) {
	val uri = mediaFile.toUri()
	Box(modifier = Modifier.fillMaxSize()) {
		when (type) {
			MessageType.IMAGE -> {
				AsyncImage(
					model = uri,
					contentDescription = null,
					contentScale = ContentScale.Fit,
					modifier = Modifier.fillMaxSize().align(Alignment.Center)
				)
			}
			MessageType.VIDEO -> {
				val playerState = rememberVideoPlayerState()

				VideoPlayer(
					modifier = Modifier.fillMaxSize().align(Alignment.Center),
					playerState = playerState
				) {
					VideoPlayerControl(state = playerState, title = "")
				}
				LaunchedEffect(Unit) {
					playerState.player.setMediaItem(MediaItem.fromUri(uri))
					playerState.player.prepare()
					playerState.player.playWhenReady = true
				}
			}
			else -> {
				Text(
					modifier = Modifier.align(Alignment.Center),
					text = "Un-supported media type",
					color = AppTheme.colors.colorTextPrimary,
					style = MaterialTheme.typography.displayLarge
				)
			}
		}
		IconButton(modifier = Modifier.align(Alignment.TopStart), onClick = onBack) {
			Icon(
				imageVector = Icons.Filled.ArrowBack,
				contentDescription = "",
				tint = AppTheme.colors.glow
			)
		}
		FloatingActionButton(
			modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
			onClick = sendMedia,
			backgroundColor = Color.Blue,
			contentColor = Color.White
		) {
			Icon(imageVector = Icons.Filled.Send, contentDescription = "")
		}
	}
}
