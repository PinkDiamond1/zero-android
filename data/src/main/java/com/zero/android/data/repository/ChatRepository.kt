package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.Channel
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

	val messages: Flow<PagingData<Message>>

	suspend fun getMessages(channel: Channel)

	suspend fun send(channel: Channel, message: DraftMessage)

	suspend fun reply(channel: Channel, id: String, message: DraftMessage)

	suspend fun updateMessage(id: String, channelId: String, text: String)

	suspend fun deleteMessage(message: Message, channel: Channel)

	suspend fun addListener(id: String)

	suspend fun removeListener(id: String)
}
