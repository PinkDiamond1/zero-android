package com.zero.android.data.repository.chat

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zero.android.common.util.MESSAGES_PAGE_LIMIT
import com.zero.android.data.conversion.toModel
import com.zero.android.models.Channel
import com.zero.android.models.Message
import com.zero.android.network.service.ChatService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MessagePagingSource
@Inject
constructor(private val chatService: ChatService, private val channel: Channel) :
	PagingSource<String, Message>() {
	override fun getRefreshKey(state: PagingState<String, Message>): String? {
		return state.anchorPosition?.let { anchorPosition ->
			return state.closestPageToPosition(anchorPosition)?.prevKey
		}
	}

	override suspend fun load(params: LoadParams<String>): LoadResult<String, Message> {
		return try {
			withContext(Dispatchers.IO) {
				val lastMessageId = params.key
				val messages =
					lastMessageId?.let {
						chatService.getMessages(channel = channel, before = lastMessageId).firstOrNull()
					}
						?: chatService.getMessages(channel = channel).firstOrNull()

				LoadResult.Page(
					data = messages?.map { it.toModel() } ?: emptyList(),
					prevKey =
					if (messages.isNullOrEmpty() || messages.size < MESSAGES_PAGE_LIMIT) null
					else messages.firstOrNull()?.id,
					nextKey = lastMessageId
				)
			}
		} catch (exception: Exception) {
			return LoadResult.Error(exception)
		}
	}
}
