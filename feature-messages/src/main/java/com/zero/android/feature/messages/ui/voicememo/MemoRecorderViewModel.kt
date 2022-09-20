package com.zero.android.feature.messages.ui.voicememo

import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.manager.MediaPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MemoRecorderViewModel
@Inject
constructor(private val mediaPlayerManager: MediaPlayerManager) : BaseViewModel() {

	private val _lastMemoPath = MutableStateFlow("")
	val lastMemoPath
		get() = _lastMemoPath.value
	val recordingState = MutableStateFlow(false)

	fun startRecording() {
		try {
			mediaPlayerManager.startRecording()
			ioScope.launch {
				_lastMemoPath.emit(mediaPlayerManager.recorderFilePath ?: "")
				recordingState.emit(true)
			}
		} catch (e: IOException) {
			ioScope.launch { recordingState.emit(false) }
		}
	}

	fun stopRecording() {
		mediaPlayerManager.stopRecording()
		ioScope.launch { recordingState.emit(false) }
	}
}
