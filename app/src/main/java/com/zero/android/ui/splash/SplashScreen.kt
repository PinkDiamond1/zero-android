package com.zero.android.ui.splash

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.imherrera.videoplayer.VideoPlayer
import com.imherrera.videoplayer.VideoPlayerControl
import com.imherrera.videoplayer.rememberVideoPlayerState
import com.zero.android.R

@Composable
fun SplashRoute(onNavigateAway: () -> Unit) {
	SplashScreen(onNavigateAway)
}

@Composable
fun SplashScreen(onNavigateAway: () -> Unit) {
	val playerState = rememberVideoPlayerState()
	val context = LocalContext.current
	val mediaUri = "android.resource://${context.packageName}/${R.raw.zero}"

	VideoPlayer(modifier = Modifier.fillMaxSize(), playerState = playerState) {
		VideoPlayerControl(state = playerState, title = "")
	}
	LaunchedEffect(Unit) {
		playerState.player.setMediaItem(MediaItem.fromUri(Uri.parse(mediaUri)))
		playerState.hideControlUi()
		playerState.hideOptionsUi()
		playerState.player.addListener(
			object : Player.Listener {
				override fun onPlaybackStateChanged(playbackState: Int) {
					super.onPlaybackStateChanged(playbackState)
					if (playbackState == Player.STATE_ENDED) {
						onNavigateAway()
					}
				}
			}
		)
		playerState.player.prepare()
		playerState.player.playWhenReady = true
	}
}
