package com.zero.android.data.repository

import com.zero.android.common.util.FileUtil
import com.zero.android.data.manager.FileManager
import com.zero.android.database.dao.MessageDao
import com.zero.android.models.ChatMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ChatMediaRepositoryImpl
@Inject
constructor(private val messageDao: MessageDao, private val fileManager: FileManager) :
	ChatMediaRepository {

	private val cachePath by lazy { fileManager.getCachePath(FileUtil.DIRECTORY_MEDIA) }

	init {
		fileManager.generateDirs(cachePath)
	}

	override val chatMedia = MutableStateFlow<List<ChatMedia>>(emptyList())

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
}
