package com.zero.android.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.zero.android.common.system.Logger
import com.zero.android.common.util.INITIAL_LOAD_SIZE
import com.zero.android.common.util.MESSAGES_PAGE_LIMIT
import com.zero.android.data.conversion.toDraft
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.manager.ImageLoader
import com.zero.android.data.mediator.MessagesRemoteMediator
import com.zero.android.database.dao.MemberDao
import com.zero.android.database.dao.MessageDao
import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.database.model.toModel
import com.zero.android.models.Channel
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.MessageType
import com.zero.android.network.model.ApiMessage
import com.zero.android.network.service.ChatService
import com.zero.android.network.service.MessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

internal class ChatRepositoryImpl
@Inject
constructor(
	private val chatService: ChatService,
	private val messageService: MessageService,
	private val imageLoader: ImageLoader,
	private val messageDao: MessageDao,
	private val memberDao: MemberDao,
	private val fileRepository: FileRepository,
	private val logger: Logger
) : ChatRepository {

	override val messages = MutableStateFlow<PagingData<Message>>(PagingData.empty())

	@OptIn(ExperimentalPagingApi::class)
	override suspend fun getMessages(channel: Channel, tillMessage: String?) {
		messages.emit(PagingData.empty())

		return Pager(
			config =
			PagingConfig(
				pageSize = MESSAGES_PAGE_LIMIT,
				initialLoadSize = MESSAGES_PAGE_LIMIT * INITIAL_LOAD_SIZE,
				prefetchDistance = 0
			),
			remoteMediator =
			MessagesRemoteMediator(chatService, messageDao, channel, tillMessage, logger),
			pagingSourceFactory = { messageDao.getByChannel(channel.id) }
		)
			.flow
			.map { data -> data.map { it.toModel() } }
			.collect { messages.emit(it) }
	}

	override suspend fun send(channel: Channel, draft: DraftMessage) {
		val flow =
			if (draft.type == MessageType.TEXT) {
				chatService.send(channel, draft)
			} else {
				sendFileMessage(channel, draft)
			}

		flow.collect { msg ->
			if (msg is ApiMessage) {
				if (msg.type == MessageType.IMAGE) msg.fileUrl?.let { imageLoader.preload(it) }

				if (msg.isDraft) {
					messageDao.upsert(
						msg.copy(id = MessageEntity.generateDraftId(msg.requestId)).toEntity().let {
							it.copy(message = it.message.copy(fileUrl = draft.file?.path))
						}
					)
				} else {
					messageDao.upsert(msg.toEntity())
				}
			} else if (msg is MessageWithRefs) {
				messageDao.upsert(msg.copy(message = msg.message.copy(fileUrl = draft.file?.path)))
			}
		}
	}

	override suspend fun resend(channel: Channel, message: Message) {
		deleteMessage(message, channel)
		send(channel, message.toDraft())
	}

	private suspend fun sendFileMessage(channel: Channel, draft: DraftMessage) = flow {
		val mentions =
			draft.mentions.takeIf { it.isNotEmpty() }?.let { memberDao.getAll(it).firstOrNull() }
		val entity =
			draft.copy(channelId = channel.id).toEntity(draft.fileRequestId!!, mentions = mentions)
		val draftId = entity.message.id
		emit(entity)

		val fileMessage: DraftMessage
		try {
			val fileUpload = fileRepository.upload(draft.file!!)
			fileMessage =
				draft.copy(
					fileUrl = fileUpload.secureUrl,
					// fileName = fileUpload.originalFilename,
					data = JSONObject(fileUpload.getDataMap(draft.type)).toString()
				)
		} catch (e: Exception) {
			runCatching { messageDao.delete(draftId) }
			throw e
		}

		emitAll(chatService.send(channel, fileMessage))
	}

	override suspend fun reply(channel: Channel, message: Message, draft: DraftMessage) {
		send(channel, draft.apply { parentMessage = message })
	}

	override suspend fun updateMessage(id: String, channelId: String, text: String) {
		val res = messageService.updateMessage(id, channelId, text)
		if (res.isSuccessful) messageDao.update(id, text)
	}

	override suspend fun markMessagesRead(channelId: String) {
		messageDao.updateDeliveryReceipt(channelId, DeliveryStatus.READ)
	}

	override suspend fun deleteMessage(message: Message, channel: Channel) {
		if (message.isSent) runCatching { chatService.deleteMessage(channel, message) }
		messageDao.delete(message.id)
	}
}
