package com.zero.android.data.repository.chat

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
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
internal class MessagesRemoteMediator(
	private val chatService: ChatService,
	private val messageDao: MessageDao,
	private val channel: Channel,
	private val logger: Logger
) : RemoteMediator<Int, MessageWithRefs>() {

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, MessageWithRefs>
	): MediatorResult {
		return try {
			// The network load method takes an optional after=<user.id>
			// parameter. For every page after the first, pass the last user
			// ID to let it continue from where it left off. For REFRESH,
			// pass null to load the first page.
			val lastMessageId =
				when (loadType) {
					LoadType.REFRESH -> null
					// In this example, you never need to prepend, since REFRESH
					// will always load the first page in the list. Immediately
					// return, reporting end of pagination.
					LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
					LoadType.APPEND -> {
						val lastItem =
							state.lastItemOrNull()
								?: return MediatorResult.Success(endOfPaginationReached = true)

						// You must explicitly check if the last item is null when
						// appending, since passing null to networkService is only
						// valid for initial load. If lastItem is null it means no
						// items were loaded after the initial REFRESH and there are
						// no more items to load.
						lastItem.message.id
					}
				}

			// Suspending network load via Retrofit. This doesn't need to be
			// wrapped in a withContext(Dispatcher.IO) { ... } block since
			// Retrofit's Coroutine CallAdapter dispatches on a worker
			// thread.
			try {
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
					endOfPaginationReached =
					response.isNullOrEmpty() || response.size < MESSAGES_PAGE_LIMIT
				)
			} catch (e: Exception) {
				logger.e(e)
				MediatorResult.Error(e)
			}
		} catch (e: IOException) {
			MediatorResult.Error(e)
		} catch (e: HttpException) {
			MediatorResult.Error(e)
		}
	}

	override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH
}
