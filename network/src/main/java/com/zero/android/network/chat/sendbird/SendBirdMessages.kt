package com.zero.android.network.chat.sendbird

import com.sendbird.android.*
import com.zero.android.common.system.Logger
import com.zero.android.common.util.MESSAGES_PAGE_LIMIT
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.network.chat.conversion.toApi
import com.zero.android.network.chat.conversion.toParams
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

internal class SendBirdMessages @Inject constructor(private val logger: Logger) {

	private val params =
		MessageListParams().apply {
			previousResultSize = MESSAGES_PAGE_LIMIT
			nextResultSize = 0
			isInclusive = true
			setReverse(true)
			setIncludeReactions(true)
			isInclusive = false
			replyTypeFilter = ReplyTypeFilter.ALL
			messagePayloadFilter =
				MessagePayloadFilter.Builder()
					.setIncludeParentMessageInfo(true)
					.setIncludeThreadInfo(true)
					.build()
		}

	fun getMessages(
		channel: BaseChannel,
		loadSize: Int = 1,
		callback: PreviousMessageListQuery.MessageListQueryResult
	) {
		val query =
			channel.createPreviousMessageListQuery().apply {
				limit = MESSAGES_PAGE_LIMIT * loadSize
				setReverse(params.shouldReverse())
				setIncludeReactions(params.shouldIncludeReactions())
				replyTypeFilter = params.replyTypeFilter
				messagePayloadFilter = params.messagePayloadFilter
			}

		if (query.hasMore()) query.load(query.limit, query.shouldReverse(), callback)
		else callback.onResult(null, null)
	}

	fun getMessages(
		channel: BaseChannel,
		beforeId: String,
		callback: BaseChannel.GetMessagesHandler
	) {
		channel.getMessagesByMessageId(beforeId.toLong(), params, callback)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	suspend fun getMessage(message: Message) =
		suspendCancellableCoroutine<BaseMessage> { coroutine ->
			if (message.type == MessageType.TEXT) {
				UserMessage.getMessage(message.toApi().toParams()) { baseMessage, e ->
					if (e != null) {
						logger.e(e)
						coroutine.resumeWithException(e)
					} else {
						coroutine.resume(baseMessage) { coroutine.resumeWithException(it) }
					}
				}
			} else {
				FileMessage.getMessage(message.toApi().toParams()) { baseMessage, e ->
					if (e != null) {
						logger.e(e)
						coroutine.resumeWithException(e)
					} else {
						coroutine.resume(baseMessage) { coroutine.resumeWithException(it) }
					}
				}
			}
		}
}
