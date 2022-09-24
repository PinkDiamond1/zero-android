package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.Channel
import com.zero.android.models.ChatMedia
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

	val messages: Flow<PagingData<Message>>

	val chatMedia: Flow<List<ChatMedia>>

	suspend fun getMessages(channel: Channel)

	suspend fun send(channel: Channel, draft: DraftMessage)

	suspend fun reply(channel: Channel, message: Message, draft: DraftMessage)

	suspend fun updateMessage(id: String, channelId: String, text: String)

	suspend fun deleteMessage(message: Message, channel: Channel)

	suspend fun getChatMedia(channelId: String)

	suspend fun downloadMedia(media: ChatMedia)
}
