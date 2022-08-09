package com.zero.android.feature.messages.helper

import com.zero.android.models.Member
import com.zero.android.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object MessageActionStateHandler {
	private val ioScope by lazy { CoroutineScope(Dispatchers.IO) }

	private val _editableMessage: MutableStateFlow<Message?> = MutableStateFlow(null)
	val editableMessage: StateFlow<Message?> = _editableMessage
	private val _selectedMessage: MutableStateFlow<Message?> = MutableStateFlow(null)
	val selectedMessage: StateFlow<Message?> = _selectedMessage
	private val _replyToMessage: MutableStateFlow<Message?> = MutableStateFlow(null)
	val replyToMessage: StateFlow<Message?> = _replyToMessage

	private val _mentionUser: MutableStateFlow<Boolean> = MutableStateFlow(false)
	val mentionUser: StateFlow<Boolean> = _mentionUser
	private val messageLastText = MutableStateFlow("")
	val messageUpdatedText = MutableStateFlow("")

	private val _mentionedUsers = mutableListOf<Member>()
	val mentionedUsers: List<Member>
		get() = _mentionedUsers

	val isActionModeStarted: Boolean
		get() = _selectedMessage.value != null

	fun setSelectedMessage(msg: Message) {
		ioScope.launch { _selectedMessage.emit(msg) }
	}

	fun editTextMessage() {
		ioScope.launch {
			if (_selectedMessage.value != null) {
				_editableMessage.emit(_selectedMessage.value)
			}
			_selectedMessage.emit(null)
			_replyToMessage.emit(null)
		}
	}

	fun replyToMessage() {
		ioScope.launch {
			if (_selectedMessage.value != null) {
				_replyToMessage.emit(_selectedMessage.value)
			}
			_selectedMessage.emit(null)
			_editableMessage.emit(null)
		}
	}

	fun closeActionMode() {
		ioScope.launch {
			_selectedMessage.emit(null)
			_editableMessage.emit(null)
			_replyToMessage.emit(null)
		}
	}

	fun reset() {
		ioScope.launch {
			_mentionUser.emit(false)
			messageLastText.emit("")
			messageUpdatedText.emit("")
		}
		closeActionMode()
	}

	fun onMessageTextChanged(message: String) {
		ioScope.launch {
			messageLastText.emit(message)
			val startMention = message.lastOrNull() == '@'
			val isMentionAlreadyStarted = _mentionUser.value
			if (isMentionAlreadyStarted) {
				val endLastMention = startMention || message.lastOrNull() == ' ' || message.isEmpty()
				if (endLastMention) {
					_mentionUser.emit(false)
					_mentionUser.emit(startMention)
				}
			} else {
				_mentionUser.emit(startMention)
			}
		}
	}

	fun onUserMentionSelected(member: Member) {
		_mentionedUsers.add(member)
		ioScope.launch {
			val lastMessage = messageLastText.value
			val newMessageText = buildString {
				val indexOfLastMention = lastMessage.indexOfLast { it == '@' }
				append(lastMessage.substring(0, indexOfLastMention + 1))
				append("${member.name?.trim()?.replace(" ","_")}")
			}
			messageUpdatedText.emit(newMessageText)
			_mentionUser.emit(false)
		}
	}

	fun getMentionQuery(text: String): String {
		return text.split("@").lastOrNull()?.split(" ")?.firstOrNull()?.trim() ?: ""
	}
}
