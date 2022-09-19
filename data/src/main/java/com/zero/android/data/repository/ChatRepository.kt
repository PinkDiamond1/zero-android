package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.*
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

	val messages: Flow<PagingData<Message>>

	val chatMedia: Flow<List<ChatMedia>>

	suspend fun getMessages(channel: Channel)

	suspend fun send(channel: Channel, message: DraftMessage)

	suspend fun reply(channel: Channel, id: String, message: DraftMessage)

	suspend fun updateMessage(id: String, channelId: String, text: String)

	suspend fun deleteMessage(message: Message, channel: Channel)

	suspend fun getChatMembers(filter: String): List<Member>

	suspend fun getChatMedia(channelId: String)

	suspend fun downloadMedia(media: ChatMedia)
}
