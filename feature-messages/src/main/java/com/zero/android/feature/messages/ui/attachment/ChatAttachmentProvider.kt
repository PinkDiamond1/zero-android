package com.zero.android.feature.messages.ui.attachment

import android.net.Uri
import com.zero.android.common.extensions.downloadFile
import com.zero.android.common.extensions.toUrl
import com.zero.android.common.extensions.withSameScope
import com.zero.android.data.manager.MediaPlayerManager
import com.zero.android.feature.messages.ui.components.VoiceMessageState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ChatAttachmentProvider
@Inject
constructor(private val fileName: String?, private val mediaPlayerManager: MediaPlayerManager) {
	private val filePath by lazy { "${mediaPlayerManager.baseFilePath}/$fileName" }
	val currentFile by lazy { File(filePath) }

	private val ioScope = CoroutineScope(Dispatchers.IO)

	val currentFileState = MutableStateFlow(VoiceMessageState.DOWNLOAD)
	val mediaFileDuration = MutableStateFlow(0)
	val currentPosition = MutableStateFlow(0)

	init {
		if (currentFile.exists()) {
			ioScope.launch { prepareMediaPlayer() }
		} else {
			ioScope.launch { currentFileState.emit(VoiceMessageState.DOWNLOAD) }
		}
	}

	private suspend fun prepareMediaPlayer() {
		ioScope.launch {
			currentFileState.emit(VoiceMessageState.STOPPED)
			val fileDuration = mediaPlayerManager.getFileDuration(currentFile)?.toInt() ?: 0
			mediaFileDuration.emit(fileDuration)
		}
	}

	fun downloadFileAndPrepare(fileUrl: String) {
		ioScope.launch {
			if (!currentFile.exists()) {
				currentFileState.emit(VoiceMessageState.DOWNLOADING)
				fileUrl.toUrl.downloadFile(currentFile.absolutePath) { progress, total ->
					if (progress == total) {
						withSameScope { prepareMediaPlayer() }
					}
				}
			}
		}
	}

	fun play() {
		if (mediaPlayerManager.mediaPlayer.isPlaying) {
			mediaPlayerManager.mediaPlayer.stop()
		}
		ioScope.launch {
			currentFileState.emit(VoiceMessageState.PLAYING)
			mediaPlayerManager.prepareMediaPlayer(Uri.fromFile(currentFile)) {
				withSameScope { currentFileState.emit(VoiceMessageState.STOPPED) }
			}
			mediaPlayerManager.mediaPlayer.start()
			updateCurrentProgress()
		}
	}

	fun seekTo(position: Float) {
		if (currentFileState.value == VoiceMessageState.PLAYING ||
			currentFileState.value == VoiceMessageState.STOPPED
		) {
			mediaPlayerManager.mediaPlayer.apply {
				pause()
				seekTo(position.times(1000).toInt())
				start()
			}
		}
	}

	private suspend fun updateCurrentProgress() {
		if (currentFileState.value == VoiceMessageState.PLAYING) {
			while (currentFileState.value == VoiceMessageState.PLAYING) {
				currentPosition.tryEmit(mediaPlayerManager.mediaPlayer.currentPosition)
			}
		}
	}

	fun stop() {
		ioScope.launch {
			currentFileState.emit(VoiceMessageState.STOPPED)
			currentPosition.emit(0)
		}
		mediaPlayerManager.mediaPlayer.stop()
	}

	fun reset() {
		ioScope.launch {
			currentFileState.tryEmit(currentFileState.value)
			currentPosition.emit(0)
		}
	}
}
