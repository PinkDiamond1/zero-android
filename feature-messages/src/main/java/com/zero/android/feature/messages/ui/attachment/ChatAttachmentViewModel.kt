package com.zero.android.feature.messages.ui.attachment

import androidx.paging.compose.LazyPagingItems
import com.zero.android.common.extensions.isValidUrl
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.repository.mediaplayer.MediaPlayerRepository
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatAttachmentViewModel
@Inject
constructor(private val mediaPlayerRepository: MediaPlayerRepository) : BaseViewModel() {

	val currentPosition
		get() = mediaPlayerRepository.mediaPlayer.currentPosition
	private val voiceMemoMediaSources = mutableMapOf<String, ChatAttachmentProvider>()
	private var lastMediaId: String? = null

	fun configure(messages: LazyPagingItems<Message>) {
		ioScope.launch {
			messages.itemSnapshotList.items
				.filter { it.type == MessageType.AUDIO }
				.map {
					if (!voiceMemoMediaSources.containsKey(it.id)) {
						val mediaSource = ChatAttachmentProvider(it.fileName, mediaPlayerRepository)
						voiceMemoMediaSources[it.id] = mediaSource
					}
				}
		}
	}

	fun getMediaSource(message: Message) =
		voiceMemoMediaSources.getOrPut(message.id) {
			ChatAttachmentProvider(message.fileName, mediaPlayerRepository)
		}

	fun dispose() {
		mediaPlayerRepository.mediaPlayer.stop()
		ioScope.launch {
			voiceMemoMediaSources.forEach { it.value.reset() }
			voiceMemoMediaSources.clear()
		}
	}

	fun downloadAndPrepareMedia(message: Message) {
		val mediaUrl = message.fileUrl
		if (mediaUrl?.isValidUrl == true) {
			getMediaSource(message).downloadFileAndPrepare(mediaUrl)
		}
	}

	fun play(message: Message) {
		voiceMemoMediaSources.values.forEach { it.reset() }
		getMediaSource(message).play()
		lastMediaId = message.id
	}

	fun stop() {
		lastMediaId?.let { voiceMemoMediaSources[lastMediaId]?.stop() }
	}

	fun seekMediaTo(message: Message, value: Float) {
		getMediaSource(message).seekTo(value)
	}
}
