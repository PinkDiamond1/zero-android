package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
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
	var timerTask: Job? by remember(message.id) { mutableStateOf(null) }

	var waveformProgress =
		if (mediaDuration > 0) {
			sliderPosition.toFloat().div(mediaDuration)
		} else 0f
	var memoAmplitudes: List<Int> by remember { mutableStateOf(emptyList()) }

	if (mediaFileState == VoiceMessageState.DOWNLOAD) {
		viewModel.downloadAndPrepareMedia(message)
	}
	if (mediaFileState == VoiceMessageState.STOPPED) {
		viewModel.getMemoAmplitudes(message) { memoAmplitudes = it }
		timerTask?.cancel()
		timerTask = null
		memoTimer = 0
		memoTimer = mediaDuration
		waveformProgress = 0f
	}

	val iconRes =
		when (mediaFileState) {
			VoiceMessageState.DOWNLOAD -> R.drawable.ic_download_circle_24
			VoiceMessageState.PLAYING -> R.drawable.ic_stop_circle_24
			else -> R.drawable.ic_play_circle_24
		}
	Row(modifier = Modifier.width(250.dp), verticalAlignment = Alignment.CenterVertically) {
		if (mediaFileState == VoiceMessageState.DOWNLOADING) {
			Spacer(modifier = Modifier.size(4.dp))
			CircularProgressIndicator(color = AppTheme.colors.glow, modifier = Modifier.size(32.dp))
			Spacer(modifier = Modifier.size(4.dp))
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
		val onWaveFormProgressChanged: (Float) -> Unit = { progress ->
			val mediaSeekPos = progress.times(mediaDuration).div(1000)
			viewModel.seekMediaTo(message, mediaSeekPos)
		}
		Box(modifier = Modifier.width(150.dp)) {
			AudioWaveform(
				modifier = Modifier.fillMaxWidth(),
				amplitudes = memoAmplitudes,
				amplitudeType = AmplitudeType.Max,
				progressBrush = SolidColor(Color.Magenta),
				waveformBrush = SolidColor(AppTheme.colors.colorTextPrimary),
				onProgressChange = { onWaveFormProgressChanged(it) },
				progress = waveformProgress,
				onProgressChangeFinished = { memoTimer = mediaDuration.minus(sliderPosition) }
			)
		}
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
