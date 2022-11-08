package com.zero.android.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.zero.android.common.system.Logger
import com.zero.android.common.util.CHANNELS_PAGE_LIMIT
import com.zero.android.common.util.INITIAL_LOAD_SIZE
import com.zero.android.data.conversion.toEntity
import com.zero.android.database.dao.ChannelDao
import com.zero.android.database.model.DirectChannelWithRefs
import com.zero.android.network.service.ChannelService
import java.io.IOException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
internal class DirectChannelsRemoteMediator(
	private val userId: String?,
	private val channelDao: ChannelDao,
	private val channelService: ChannelService,
	private val logger: Logger
) : RemoteMediator<Int, DirectChannelWithRefs>() {

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, DirectChannelWithRefs>
	): MediatorResult {
		return try {
			val lastChannelId =
				when (loadType) {
					LoadType.REFRESH -> null
					LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
					LoadType.APPEND -> {
						val lastItem =
							state.lastItemOrNull()
								?: return MediatorResult.Success(endOfPaginationReached = true)
						lastItem.channel.id
					}
				}

			val response =
				lastChannelId?.let {
					channelService.getDirectChannels(before = it, refresh = loadType == LoadType.REFRESH)
				}
					?: channelService.getDirectChannels(
						loadSize = INITIAL_LOAD_SIZE,
						refresh = loadType == LoadType.REFRESH
					)

			response.map { it.toEntity(userId) }.let { channelDao.upsert(*it.toTypedArray()) }

			logger.d("Loading Direct Channels: $loadType - $lastChannelId: ${response.size}")

			MediatorResult.Success(
				endOfPaginationReached = response.isEmpty() || response.size < CHANNELS_PAGE_LIMIT
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
