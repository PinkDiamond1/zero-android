package com.zero.android.feature.messages.ui.components

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
import kotlin.time.Duration.Companion.seconds

enum class VoiceMessageState {
	DOWNLOAD,
	DOWNLOADING,
	PLAYING,
	STOPPED
}

@Composable
fun VoiceMessage(message: Message, viewModel: ChatAttachmentViewModel) {
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
				memoTimer = mediaDuration
				timerTask?.cancel()
				R.drawable.ic_play_circle_24
			}
		}
	Row(modifier = Modifier.wrapContentWidth()) {
		if (mediaFileState == VoiceMessageState.DOWNLOADING) {
			CircularProgressIndicator(
				color = AppTheme.colors.glow,
				modifier = Modifier.size(32.dp).align(Alignment.CenterVertically)
			)
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
										delay(1.seconds)
										memoTimer -= 1000
									}
								}
						}
						VoiceMessageState.PLAYING -> viewModel.stop()
						else -> {}
					}
				},
				modifier = Modifier.align(Alignment.CenterVertically)
			) {
				Icon(
					modifier = Modifier.size(32.dp),
					painter = painterResource(iconRes),
					contentDescription = null,
					tint = Color.White
				)
			}
		}
		Spacer(modifier = Modifier.size(4.dp))
		Slider(
			modifier = Modifier.width(150.dp).align(Alignment.CenterVertically),
			value = sliderPosition,
			onValueChange = { viewModel.seekMediaTo(message, it) },
			onValueChangeFinished = {
				val currentPos = sliderPosition.times(1000).toInt()
				memoTimer = mediaDuration.minus(currentPos)
			},
			valueRange = 0f..mediaDuration.div(1000).toFloat(),
			colors =
			SliderDefaults.colors(
				thumbColor = Color.White,
				activeTrackColor = AppTheme.colors.colorTextPrimary
			)
		)
		Spacer(modifier = Modifier.size(4.dp))
		Text(
			text =
			if (memoTimer > 0) {
				memoTimer.convertDurationToString()
			} else "-",
			modifier = Modifier.align(Alignment.CenterVertically),
			style = MaterialTheme.typography.bodyMedium
		)
	}
}
