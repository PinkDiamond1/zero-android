package com.zero.android.feature.messages.ui.attachment

import android.net.Uri
import com.zero.android.common.extensions.downloadFile
import com.zero.android.common.extensions.toUrl
import com.zero.android.data.manager.MediaPlayerManager
import com.zero.android.feature.messages.ui.components.VoiceMessageState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ChatAttachmentProvider
@Inject
constructor(private val fileName: String?, private val mediaPlayerManager: MediaPlayerManager) {
	private val filePath by lazy { "${mediaPlayerManager.baseFilePath}/$fileName" }
	private val currentFile by lazy { File(filePath) }

	private val ioJob = SupervisorJob()
	private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
		throwable.printStackTrace()
	}
	private val ioScope = CoroutineScope(Dispatchers.IO + ioJob + exceptionHandler)

	val currentFileState = MutableStateFlow(VoiceMessageState.DOWNLOAD)
	val mediaFileDuration = MutableStateFlow(0)
	val currentPosition = MutableStateFlow(0f)

	init {
		if (currentFile.exists()) {
			prepareMediaPlayer()
		} else {
			ioScope.launch { currentFileState.emit(VoiceMessageState.DOWNLOAD) }
		}
	}

	private fun prepareMediaPlayer() {
		ioScope.launch {
			currentFileState.emit(VoiceMessageState.STOPPED)
			val fileDuration = mediaPlayerManager.getFileDuration(currentFile)?.toInt() ?: 0
			mediaFileDuration.emit(fileDuration)
		}
	}

	fun downloadFileAndPrepare(fileUrl: String) {
		ioScope.launch {
			currentFileState.emit(VoiceMessageState.DOWNLOADING)
			fileUrl.toUrl.downloadFile(currentFile.absolutePath) { progress, total ->
				if (progress == total) {
					prepareMediaPlayer()
				}
			}
		}
	}

	fun play() {
		if (mediaPlayerManager.mediaPlayer.isPlaying) {
			mediaPlayerManager.mediaPlayer.stop()
		}
		ioScope.launch { currentFileState.emit(VoiceMessageState.PLAYING) }
		mediaPlayerManager.prepareMediaPlayer(Uri.fromFile(currentFile)) {
			ioScope.launch { currentFileState.emit(VoiceMessageState.STOPPED) }
		}
		mediaPlayerManager.mediaPlayer.start()
		updateCurrentProgress()
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

	private fun updateCurrentProgress() {
		if (currentFileState.value == VoiceMessageState.PLAYING) {
			ioScope.launch {
				while (currentFileState.value == VoiceMessageState.PLAYING) {
					delay(100)
					currentPosition.emit((mediaPlayerManager.mediaPlayer.currentPosition / 1000).toFloat())
				}
			}
		}
	}

	fun stop() {
		ioScope.launch {
			currentFileState.emit(VoiceMessageState.STOPPED)
			currentPosition.emit(0f)
		}
		mediaPlayerManager.mediaPlayer.stop()
	}

	fun reset() {
		ioScope.launch {
			currentFileState.emit(VoiceMessageState.STOPPED)
			currentPosition.emit(0f)
		}
	}
}
