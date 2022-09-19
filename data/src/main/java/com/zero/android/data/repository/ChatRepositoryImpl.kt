package com.zero.android.data.repository

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import androidx.paging.*
import com.zero.android.common.extensions.isValidUrl
import com.zero.android.common.system.Logger
import com.zero.android.common.util.MESSAGES_PAGE_LIMIT
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.repository.chat.MessagesRemoteMediator
import com.zero.android.database.dao.MessageDao
import com.zero.android.database.model.toModel
import com.zero.android.models.*
import com.zero.android.models.enums.MessageType
import com.zero.android.network.model.ApiMessage
import com.zero.android.network.model.toMember
import com.zero.android.network.service.ChatMediaService
import com.zero.android.network.service.ChatService
import com.zero.android.network.service.MessageService
import com.zero.android.network.util.NetworkMediaUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class ChatRepositoryImpl
@Inject
constructor(
	@ApplicationContext private val applicationContext: Context,
	private val chatService: ChatService,
	private val messageService: MessageService,
	private val chatMediaService: ChatMediaService,
	private val networkMediaUtil: NetworkMediaUtil,
	private val messageDao: MessageDao,
	private val logger: Logger
) : ChatRepository {

	override val messages = MutableStateFlow<PagingData<Message>>(PagingData.empty())
	override val chatMedia = MutableStateFlow<List<ChatMedia>>(emptyList())

	@OptIn(ExperimentalPagingApi::class)
	override suspend fun getMessages(channel: Channel) {
		messages.emit(PagingData.empty())

		return Pager(
			config = PagingConfig(pageSize = MESSAGES_PAGE_LIMIT, prefetchDistance = 0),
			remoteMediator = MessagesRemoteMediator(chatService, messageDao, channel, logger),
			pagingSourceFactory = { messageDao.getByChannel(channel.id) }
		)
			.flow
			.map { data -> data.map { it.toModel() } }
			.collect { messages.emit(it) }
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
				message.copy(
					fileUrl = fileUpload.secureUrl,
					fileName = fileUpload.originalFilename,
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

	// TODO: Use MemberDao like in `ChannelRepositoryImpl.getDirectChannel()`
	override suspend fun getChatMembers(filter: String): List<Member> {
		val filterObject = JSONObject().apply { putOpt("filter", filter) }
		val networkUsers = messageService.getMembers(filterObject.toString())
		return networkUsers.body()?.map { it.toMember() } ?: emptyList()
	}

	override suspend fun getChatMedia(channelId: String) {
		messageDao
			.getMediaByChannel(channelId)
			.map { data ->
				data.map { message ->
					ChatMedia(message.message.id, message.message.fileUrl, message.message.type)
				}
			}
			.collect { chatMedia.emit(it) }
	}

	override suspend fun downloadMedia(media: ChatMedia) {
		val mediaUrl = media.mediaUrl
		val fileExtension = media.mediaUrl?.split('.')?.lastOrNull() ?: "jpg"
		val fileName = mediaUrl?.split('/')?.lastOrNull() ?: "File_${System.nanoTime()}.$fileExtension"
		if (!mediaUrl.isNullOrEmpty() && mediaUrl.isValidUrl) {
			downloadChatMedia(fileName, mediaUrl)
		}
	}

	private fun downloadChatMedia(
		fileName: String,
		mediaUrl: String,
		downloadDirectory: String = Environment.DIRECTORY_DOWNLOADS
	) {
		val request =
			DownloadManager.Request(Uri.parse(mediaUrl))
				.setTitle(fileName)
				.setDescription("Downloading...")
				.setDestinationInExternalPublicDir(downloadDirectory, "/$fileName")
				.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
				.setAllowedOverMetered(true)
				.setAllowedNetworkTypes(
					DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
				)
		val downloadManager = applicationContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
		downloadManager.enqueue(request)
	}
}
