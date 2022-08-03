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
	PagingSource<Int, Message>() {
	override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
		return state.anchorPosition
	}

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
		return try {
			withContext(Dispatchers.IO) {
				val nextPage = params.key ?: 1
				val messages = chatService.getMessages(channel = channel).firstOrNull()
				LoadResult.Page(
					data = messages?.map { it.toModel() } ?: emptyList(),
					prevKey = if (nextPage == 1) null else nextPage - 1,
					nextKey =
					if (messages.isNullOrEmpty() || messages.size < MESSAGES_PAGE_LIMIT) null
					else nextPage + 1
				)
			}
		} catch (exception: Exception) {
			return LoadResult.Error(exception)
		}
	}
}
