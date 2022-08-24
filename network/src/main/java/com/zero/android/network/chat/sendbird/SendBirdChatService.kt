package com.zero.android.network.chat.sendbird

import com.sendbird.android.FileMessageParams
import com.sendbird.android.UserMessageParams
import com.zero.android.common.extensions.callbackFlowWithAwait
import com.zero.android.common.extensions.withSameScope
import com.zero.android.common.system.Logger
import com.zero.android.models.Channel
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import com.zero.android.network.chat.conversion.toApi
import com.zero.android.network.chat.conversion.toParams
import com.zero.android.network.model.ApiMessage
import com.zero.android.network.service.ChatService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class SendBirdChatService(
	private val logger: Logger,
	private val messages: SendBirdMessages = SendBirdMessages(logger)
) : SendBirdBaseService(), ChatService {

	override suspend fun getMessages(channel: Channel, loadSize: Int) =
		callbackFlowWithAwait<List<ApiMessage>> {
			messages.getMessages(getChannel(channel), loadSize) { messages, e ->
				if (e != null) {
					logger.e(e)
					throw e
				} else {
					trySend(messages?.map { it.toApi() } ?: emptyList())
				}
			}
		}

	override suspend fun getMessages(channel: Channel, before: String) =
		callbackFlowWithAwait<List<ApiMessage>> {
			messages.getMessages(getChannel(channel), before) { messages, e ->
				if (e != null) {
					logger.e(e)
					throw e
				} else {
					trySend(messages?.map { it.toApi() } ?: emptyList())
				}
			}
		}

	override suspend fun send(channel: Channel, message: DraftMessage) =
		suspendCancellableCoroutine { coroutine ->
			val params = message.toParams()
			val sbChannel = runBlocking { getChannel(channel) }
			if (params is FileMessageParams) {
				sbChannel.sendFileMessage(params) { fileMessage, e ->
					if (e != null) {
						logger.e("Failed to send file message", e)
						coroutine.resumeWithException(e)
					}
					coroutine.resume(fileMessage.toApi())
				}
			} else if (params is UserMessageParams) {
				sbChannel.sendUserMessage(params) { userMessage, e ->
					if (e != null) {
						logger.e("Failed to send text message", e)
						coroutine.resumeWithException(e)
					}
					coroutine.resume(userMessage.toApi())
				}
			}
		}

	override suspend fun reply(channel: Channel, id: String, message: DraftMessage) =
		send(channel, message.apply { parentMessageId = id })

	override suspend fun deleteMessage(channel: Channel, message: Message) =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				getChannel(channel).deleteMessage(messages.getMessage(message)) {
					if (it != null) {
						logger.e("Failed to delete message", it)
						coroutine.resumeWithException(it)
					} else {
						coroutine.resume(Unit)
					}
				}
			}
		}
}
