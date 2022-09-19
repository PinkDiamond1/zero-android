package com.zero.android.feature.messages.ui.mediaviewer

import androidx.lifecycle.SavedStateHandle
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.repository.ChatRepository
import com.zero.android.feature.messages.navigation.ChatMediaViewerDestination
import com.zero.android.models.ChatMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ChatMediaViewModel
@Inject
constructor(savedStateHandle: SavedStateHandle, private val chatRepository: ChatRepository) :
	BaseViewModel() {

	private val channelId: String =
		checkNotNull(savedStateHandle[ChatMediaViewerDestination.ARG_CHANNEL_ID])
	val messageId: String = checkNotNull(savedStateHandle[ChatMediaViewerDestination.ARG_MESSAGE_ID])

	val chatMedia = chatRepository.chatMedia

	fun getChatMedia() {
		ioScope.launch { chatRepository.getChatMedia(channelId) }
	}

	fun downloadMedia(media: ChatMedia) {
		ioScope.launch { chatRepository.downloadMedia(media) }
	}
}
