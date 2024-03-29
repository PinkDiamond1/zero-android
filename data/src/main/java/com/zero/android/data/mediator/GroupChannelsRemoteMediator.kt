package com.zero.android.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.zero.android.common.system.Logger
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.extensions.initialPageSize
import com.zero.android.database.dao.ChannelDao
import com.zero.android.database.model.ChannelWithRefs
import com.zero.android.network.service.ChannelService
import java.io.IOException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
internal class GroupChannelsRemoteMediator(
	private val networkId: String,
	private val channelDao: ChannelDao,
	private val channelService: ChannelService,
	private val logger: Logger,
	private val search: String? = null
) : RemoteMediator<Int, ChannelWithRefs>() {

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, ChannelWithRefs>
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
					channelService.getGroupChannels(
						networkId = networkId,
						before = it,
						limit = state.config.pageSize,
						searchName = search,
						refresh = loadType == LoadType.REFRESH
					)
				}
					?: channelService.getGroupChannels(
						networkId = networkId,
						limit = state.config.initialPageSize,
						searchName = search,
						refresh = loadType == LoadType.REFRESH
					)

			response.map { it.toEntity() }.let { channelDao.upsert(*it.toTypedArray()) }

			logger.d("Loading Group Channels: $loadType - $lastChannelId: ${response.size}")

			MediatorResult.Success(
				endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
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
