package com.zero.android.feature.messages.ui.messages

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zero.android.common.system.NetworkManager
import com.zero.android.common.system.NotificationManager
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.ui.data
import com.zero.android.common.util.ConnectionState
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.manager.MediaManager
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.ChatRepository
import com.zero.android.data.repository.MemberRepository
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.feature.messages.navigation.MessagesDestination
import com.zero.android.feature.messages.util.MessageUtil
import com.zero.android.models.Channel
import com.zero.android.models.DraftMessage
import com.zero.android.models.Member
import com.zero.android.models.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MessagesViewModel
@Inject
constructor(
	savedStateHandle: SavedStateHandle,
	@ApplicationContext context: Context,
	private val preferences: Preferences,
	private val chatRepository: ChatRepository,
	private val memberRepository: MemberRepository,
	private val channelRepository: ChannelRepository,
	private val mediaManager: MediaManager,
	notificationManager: NotificationManager
) : BaseViewModel() {

	val channelId: String = checkNotNull(savedStateHandle[MessagesDestination.ARG_CHANNEL_ID])
	val isGroupChannel: Boolean =
		checkNotNull(savedStateHandle[MessagesDestination.ARG_IS_GROUP_CHANNEL])

	val loggedInUserId = runBlocking(Dispatchers.IO) { preferences.userId() }
	val lastMessage = channelRepository.lastMessage

	private val _channel = MutableStateFlow<Result<Channel>>(Result.Loading)

	val messages = chatRepository.messages.onEach { markChannelRead() }.cachedIn(viewModelScope)
	private val _messagesResult = messages.asResult()

	private val _textSearch = MutableStateFlow("")
	private val _chatMentionUsers = MutableStateFlow<List<Member>>(emptyList())
	val chatMentionUsers: StateFlow<List<Member>> = _chatMentionUsers

	val uiState: StateFlow<ChatScreenUiState> =
		combine(_channel, _messagesResult) { channelResult, messagesResult ->
			ChatScreenUiState(channelUiState = channelResult, messagesUiState = messagesResult)
		}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(1_000),
				initialValue =
				ChatScreenUiState(
					channelUiState = Result.Loading,
					messagesUiState = Result.Loading
				)
			)

	init {
		viewModelScope.launch {
			_textSearch.asStateFlow().debounce(1500).collectLatest { query ->
				ioScope.launch {
					val members = memberRepository.getMembers(query).first()
					_chatMentionUsers.emit(members.distinct())
				}
			}
		}

		viewModelScope.launch {
			NetworkManager.observeConnection(context).collect {
				if (it is ConnectionState.Available) refreshChat()
			}
		}

		notificationManager.removeMessageNotifications(channelId = channelId)
	}

	fun onSearchTextChanged(text: String) {
		_textSearch.value = MessageActionStateHandler.getMentionQuery(text)
	}

	fun loadChannel() {
		ioScope.launch {
			val request =
				if (isGroupChannel) {
					channelRepository.getGroupChannel(channelId)
				} else {
					channelRepository.getDirectChannel(channelId)
				}

			request.firstOrNull()?.let { channel ->
				_channel.emit(Result.Success(channel))
				markChannelRead()
				chatRepository.getMessages(channel)
			}
		}
	}

	fun configureChat() {
		markChannelRead()
		updateMessage()
	}

	private fun refreshChat(tillMessage: String? = null) {
		ioScope.launch { _channel.data()?.let { chatRepository.getMessages(it, tillMessage) } }
	}

	private fun getLastMessageStatus() {
		ioScope.launch {
			_channel.data()?.let { channel ->
				lastMessage.firstOrNull()?.let { message ->
					if (message.author?.id == loggedInUserId && channel.memberCount == 2) {
						val chatMembers = channel.members.filter { it.id != loggedInUserId }.map { it.id }
						val readMembers = channelRepository.getReadMembers(channel.id).map { it.id }
						if (readMembers.containsAll(chatMembers)) {
							// chatRepository.markRead(message)
							chatRepository.markMessagesRead(channel.id)
						}
					}
				}
			}
		}
	}

	fun sendMessage(message: DraftMessage) {
		ioScope.launch {
			_channel.data()?.let { chatRepository.send(it, message) }
			markChannelRead()
			updateMessage()
		}
	}

	fun deleteMessage(message: Message) {
		ioScope.launch {
			_channel.data()?.let { chatRepository.deleteMessage(message, it) }
			updateMessage()
		}
	}

	fun updateMessage(message: Message) {
		ioScope.launch {
			_channel.data()?.let { channel ->
				val newText = message.message ?: ""
				val updatedMessage =
					message.copy(
						message = MessageUtil.prepareMessage(newText, channel.members),
						mentions = MessageUtil.getMentionedUsers(newText, channel.members)
					)
				chatRepository.updateMessage(updatedMessage.id, channelId, updatedMessage.message ?: "")
			}
		}
	}

	fun retryMessage(message: Message) {
		ioScope.launch { _channel.data()?.let { chatRepository.resend(it, message) } }
	}

	fun replyToMessage(message: Message, replyMessage: DraftMessage) {
		ioScope.launch { _channel.data()?.let { chatRepository.reply(it, message, replyMessage) } }
	}

	private fun markChannelRead() {
		ioScope.launch { _channel.data()?.let { channelRepository.markChannelRead(it) } }
	}

	private fun updateMessage() {
		ioScope.launch {
			channelRepository.getLastMessage(channelId)
			getLastMessageStatus()
		}
	}

	fun copyMediaFile(mediaMessage: Message) {
		ioScope.launch { mediaManager.copyToClipboard(mediaMessage) }
	}
}
