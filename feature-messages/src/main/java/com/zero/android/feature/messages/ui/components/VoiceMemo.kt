package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.common.extensions.convertDurationToString
import com.zero.android.feature.messages.ui.attachment.ChatAttachmentViewModel
import com.zero.android.models.Message
import com.zero.android.ui.theme.AppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class VoiceMessageState {
	DOWNLOAD,
	DOWNLOADING,
	PLAYING,
	STOPPED
}

@Composable
fun VoiceMessage(
	message: Message,
	isUserMe: Boolean,
	viewModel: ChatAttachmentViewModel,
	darkTheme: Boolean = isSystemInDarkTheme()
) {
	val controlsColor =
		if (darkTheme) {
			Color.White
		} else {
			if (isUserMe) Color.White else Color.Black
		}
	val coroutineScope = rememberCoroutineScope()
	val mediaSourceProvider by
	remember(message.id) { mutableStateOf(viewModel.getMediaSource(message)) }
	val mediaFileState by mediaSourceProvider.currentFileState.collectAsState()
	val sliderPosition by mediaSourceProvider.currentPosition.collectAsState()
	val mediaDuration by mediaSourceProvider.mediaFileDuration.collectAsState()

	var memoTimer by remember { mutableStateOf(mediaDuration) }
	var timerTask: Job? = null

	val iconRes =
		when (mediaFileState) {
			VoiceMessageState.DOWNLOAD -> R.drawable.ic_download_circle_24
			VoiceMessageState.PLAYING -> R.drawable.ic_stop_circle_24
			else -> {
				timerTask?.cancel()
				memoTimer = 0
				memoTimer = mediaDuration
				R.drawable.ic_play_circle_24
			}
		}
	Row(modifier = Modifier.width(250.dp), verticalAlignment = Alignment.CenterVertically) {
		if (mediaFileState == VoiceMessageState.DOWNLOADING) {
			CircularProgressIndicator(color = AppTheme.colors.glow, modifier = Modifier.size(32.dp))
		} else {
			IconButton(
				onClick = {
					when (mediaFileState) {
						VoiceMessageState.DOWNLOAD -> viewModel.downloadAndPrepareMedia(message)
						VoiceMessageState.STOPPED -> {
							memoTimer = mediaDuration
							viewModel.play(message)
							timerTask =
								coroutineScope.launch {
									while (memoTimer > 0) {
										delay(1000)
										memoTimer -= 1000
									}
								}
						}
						VoiceMessageState.PLAYING -> viewModel.stop()
						else -> {}
					}
				}
			) {
				Icon(
					modifier = Modifier.size(32.dp),
					painter = painterResource(iconRes),
					contentDescription = null,
					tint = controlsColor
				)
			}
		}
		Spacer(modifier = Modifier.size(4.dp))
		Slider(
			modifier = Modifier.width(150.dp),
			value = sliderPosition,
			onValueChange = { viewModel.seekMediaTo(message, it) },
			onValueChangeFinished = {
				val currentPos = sliderPosition.times(1000).toInt()
				memoTimer = mediaDuration.minus(currentPos)
			},
			valueRange = 0f..mediaDuration.div(1000).toFloat(),
			colors =
			SliderDefaults.colors(
				thumbColor = controlsColor,
				activeTrackColor = AppTheme.colors.colorTextPrimary
			)
		)
		Spacer(modifier = Modifier.size(4.dp))
		Text(
			text =
			if (memoTimer > 0) {
				memoTimer.convertDurationToString()
			} else "00:00",
			style = MaterialTheme.typography.bodyMedium,
			color = controlsColor
		)
	}
}
