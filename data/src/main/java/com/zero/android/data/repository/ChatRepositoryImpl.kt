package com.zero.android.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.zero.android.common.system.Logger
import com.zero.android.common.util.MESSAGES_PAGE_LIMIT
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.conversion.toModel
import com.zero.android.data.repository.chat.MessageListener
import com.zero.android.data.repository.chat.MessagesRemoteMediator
import com.zero.android.database.dao.MessageDao
import com.zero.android.database.model.toModel
import com.zero.android.models.Channel
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.network.model.ApiMessage
import com.zero.android.network.service.ChatMediaService
import com.zero.android.network.service.ChatService
import com.zero.android.network.service.MessageService
import com.zero.android.network.util.NetworkMediaUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class ChatRepositoryImpl
@Inject
constructor(
	private val chatService: ChatService,
	private val messageService: MessageService,
	private val chatMediaService: ChatMediaService,
	private val networkMediaUtil: NetworkMediaUtil,
	private val messageDao: MessageDao,
	private val logger: Logger
) : ChatRepository {

	override val messages = MutableStateFlow<PagingData<Message>>(PagingData.empty())

	override suspend fun addListener(id: String) =
		chatService.addListener(id, MessageListener(messageDao))

	override suspend fun removeListener(id: String) = chatService.removeListener(id)

	override suspend fun getMessages(channel: Channel, lastMessageId: String): Flow<List<Message>> {
		return chatService.getMessages(channel, lastMessageId).map { messages ->
			messages.map { it.toModel() }
		}
	}

	@OptIn(ExperimentalPagingApi::class)
	override suspend fun getMessages(channel: Channel): Flow<PagingData<Message>> {
		messages.emit(PagingData.empty())

		return Pager(
			config = PagingConfig(pageSize = MESSAGES_PAGE_LIMIT),
			remoteMediator = MessagesRemoteMediator(chatService, messageDao, channel, logger),
			pagingSourceFactory = { messageDao.getByChannel(channel.id) }
		)
			.flow
			.map { data -> data.map { it.toModel() } }
			.apply { collect(messages) }
	}

	override suspend fun send(channel: Channel, message: DraftMessage) {
		val msg =
			if (message.type == MessageType.TEXT) {
				chatService.send(channel, message)
			} else {
				sendFileMessage(channel, message)
			}
		messageDao.upsert(msg.toEntity())
	}

	private suspend fun sendFileMessage(channel: Channel, message: DraftMessage): ApiMessage {
		val uploadInfo = chatMediaService.getUploadInfo()
		val fileMessage =
			if (uploadInfo.apiUrl.isNotEmpty() && uploadInfo.query != null) {
				val fileUpload =
					chatMediaService.uploadMediaFile(
						networkMediaUtil.getUploadUrl(uploadInfo),
						networkMediaUtil.getUploadBody(message.file!!)
					)
				DraftMessage(
					channelId = channel.id,
					author = message.author,
					type = message.type,
					mentionType = message.mentionType,
					fileUrl = fileUpload.secureUrl,
					fileName = fileUpload.originalFilename,
					fileMimeType = fileUpload.type,
					createdAt = message.createdAt,
					updatedAt = message.updatedAt,
					status = message.status,
					data = JSONObject(fileUpload.getDataMap(message.type)).toString()
				)
			} else {
				Timber.e("Upload Info is required for file upload")
				message
			}
		return chatService.send(channel, fileMessage)
	}

	override suspend fun reply(channel: Channel, id: String, message: DraftMessage) {
		send(channel, message.apply { parentMessageId = id })
	}

	override suspend fun updateMessage(id: String, channelId: String, text: String) {
		val res = messageService.updateMessage(id, channelId, text)
		if (res.isSuccessful) messageDao.update(id, text)
	}

	override suspend fun deleteMessage(message: Message, channel: Channel) {
		chatService.deleteMessage(channel, message)
		messageDao.delete(message.id)
	}
}
