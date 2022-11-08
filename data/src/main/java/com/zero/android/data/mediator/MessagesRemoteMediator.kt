package com.zero.android.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.zero.android.common.system.Logger
import com.zero.android.common.util.INITIAL_LOAD_SIZE
import com.zero.android.common.util.MESSAGES_PAGE_LIMIT
import com.zero.android.data.conversion.toEntity
import com.zero.android.database.dao.MessageDao
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.models.Channel
import com.zero.android.network.service.ChatService
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
internal class MessagesRemoteMediator(
	private val chatService: ChatService,
	private val messageDao: MessageDao,
	private val channel: Channel,
	private var tillMessage: String?,
	private val logger: Logger
) : RemoteMediator<Int, MessageWithRefs>() {

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, MessageWithRefs>
	): MediatorResult {
		return try {
			val lastMessageId =
				tillMessage
					?: when (loadType) {
						LoadType.REFRESH -> null
						LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
						LoadType.APPEND -> {
							val lastItem =
								state.lastItemOrNull()
									?: return MediatorResult.Success(endOfPaginationReached = true)
							lastItem.message.id
						}
					}
			tillMessage = null

			val response =
				lastMessageId?.let {
					chatService.getMessages(channel = channel, before = it).firstOrNull()
				}
					?: chatService
						.getMessages(channel = channel, loadSize = INITIAL_LOAD_SIZE)
						.firstOrNull()

			response?.map { it.toEntity() }?.let { messageDao.upsert(*it.toTypedArray()) }

			logger.d("Loading Messages: $loadType - $lastMessageId: ${response?.size ?: 0}")

			MediatorResult.Success(
				endOfPaginationReached = response.isNullOrEmpty() || response.size < MESSAGES_PAGE_LIMIT
			)
		} catch (e: UnknownHostException) {
			MediatorResult.Error(e)
		} catch (e: IOException) {
			MediatorResult.Error(e)
		} catch (e: Exception) {
			logger.e(e)
			MediatorResult.Error(e)
		}
	}

	override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH
}
