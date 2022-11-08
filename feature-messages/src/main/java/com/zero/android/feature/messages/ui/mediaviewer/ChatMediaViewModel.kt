package com.zero.android.feature.messages.ui.mediaviewer

import androidx.lifecycle.SavedStateHandle
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.manager.MediaManager
import com.zero.android.data.repository.ChatMediaRepository
import com.zero.android.feature.messages.navigation.ChatMediaViewerDestination
import com.zero.android.models.ChatMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatMediaViewModel
@Inject
constructor(
	savedStateHandle: SavedStateHandle,
	private val chatMediaRepository: ChatMediaRepository,
	private val mediaManager: MediaManager
) : BaseViewModel() {

	private val channelId: String =
		checkNotNull(savedStateHandle[ChatMediaViewerDestination.ARG_CHANNEL_ID])
	val messageId: String = checkNotNull(savedStateHandle[ChatMediaViewerDestination.ARG_MESSAGE_ID])

	val chatMedia = chatMediaRepository.chatMedia

	fun getChatMedia() {
		ioScope.launch { chatMediaRepository.getChatMedia(channelId) }
	}

	fun downloadMedia(media: ChatMedia) {
		ioScope.launch { mediaManager.download(media) }
	}
}
