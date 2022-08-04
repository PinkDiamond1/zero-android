package com.zero.android.network.service

import com.zero.android.models.Channel
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import com.zero.android.network.chat.ChatListener
import com.zero.android.network.model.ApiMessage
import kotlinx.coroutines.flow.Flow

interface ChatService {

	suspend fun addListener(channelId: String, listener: ChatListener)

	suspend fun removeListener(channelId: String)

	suspend fun getMessages(channel: Channel, loadSize: Int = 1): Flow<List<ApiMessage>>

	suspend fun getMessages(channel: Channel, before: String): Flow<List<ApiMessage>>

	suspend fun send(channel: Channel, message: DraftMessage): ApiMessage

	suspend fun reply(channel: Channel, id: String, message: DraftMessage): ApiMessage

	suspend fun deleteMessage(channel: Channel, message: Message)
}
