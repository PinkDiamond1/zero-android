package com.zero.android.network.service

import com.zero.android.models.Channel
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import com.zero.android.network.model.ApiMessage
import kotlinx.coroutines.flow.Flow

interface ChatService {

	suspend fun getMessages(channel: Channel, limit: Int): Flow<List<ApiMessage>>

	suspend fun getMessages(channel: Channel, limit: Int, before: String): Flow<List<ApiMessage>>

	suspend fun send(channel: Channel, message: DraftMessage): Flow<ApiMessage>

	suspend fun reply(channel: Channel, message: Message, draft: DraftMessage): Flow<ApiMessage>

	suspend fun deleteMessage(channel: Channel, message: Message)
}
