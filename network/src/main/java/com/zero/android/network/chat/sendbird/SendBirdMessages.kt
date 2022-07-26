package com.zero.android.network.chat.sendbird

import com.sendbird.android.BaseChannel
import com.sendbird.android.MessageListParams
import com.sendbird.android.MessagePayloadFilter
import com.sendbird.android.PreviousMessageListQuery
import com.sendbird.android.ReplyTypeFilter
import com.zero.android.common.util.MESSAGES_PAGE_LIMIT

internal class SendBirdMessages {

	private var channel: BaseChannel? = null
	private lateinit var query: PreviousMessageListQuery

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

	private fun init(channel: BaseChannel) {
		if (this.channel?.url == channel.url) return
		this.channel = channel

		query =
			channel.createPreviousMessageListQuery().apply {
				limit = MESSAGES_PAGE_LIMIT
				setReverse(params.shouldReverse())
				setIncludeReactions(params.shouldIncludeReactions())
				replyTypeFilter = params.replyTypeFilter
				messagePayloadFilter = params.messagePayloadFilter
			}
	}

	fun getMessages(channel: BaseChannel, callback: PreviousMessageListQuery.MessageListQueryResult) {
		init(channel)

		if (query.hasMore()) query.load(callback) else callback.onResult(null, null)
	}

	fun getMessages(
		channel: BaseChannel,
		beforeId: String,
		callback: BaseChannel.GetMessagesHandler
	) {
		init(channel)

		channel.getMessagesByMessageId(beforeId.toLong(), params, callback)
	}
}
