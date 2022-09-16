package com.zero.android.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.zero.android.common.extensions.channelFlowWithAwait
import com.zero.android.common.system.Logger
import com.zero.android.common.util.CHANNELS_PAGE_LIMIT
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.conversion.toModel
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.repository.chat.DirectChannelsRemoteMediator
import com.zero.android.data.repository.chat.GroupChannelsRemoteMediator
import com.zero.android.database.dao.ChannelDao
import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.toModel
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Member
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.ChannelType
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel
import com.zero.android.network.service.ChannelService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ChannelRepositoryImpl
@Inject
constructor(
	private val channelDao: ChannelDao,
	private val channelService: ChannelService,
	private val logger: Logger,
	preferences: Preferences
) : ChannelRepository {

	private val userId = runBlocking { preferences.userId() }

	@OptIn(ExperimentalPagingApi::class)
	override fun getDirectChannels(search: String?): Flow<PagingData<DirectChannel>> {
		return Pager(
			config = PagingConfig(pageSize = CHANNELS_PAGE_LIMIT, prefetchDistance = 3),
			remoteMediator =
			DirectChannelsRemoteMediator(userId, channelDao, channelService, logger),
			pagingSourceFactory = {
				if (search.isNullOrEmpty()) channelDao.getDirectChannels()
				else channelDao.searchDirectChannels(search)
			}
		)
			.flow.map { data -> data.map { it.toModel() } }
	}

	@OptIn(ExperimentalPagingApi::class)
	override fun getGroupChannels(
		networkId: String,
		category: String?,
		search: String?
	): Flow<PagingData<GroupChannel>> {
		return Pager(
			config = PagingConfig(pageSize = CHANNELS_PAGE_LIMIT, prefetchDistance = 3),
			remoteMediator =
			GroupChannelsRemoteMediator(
				networkId = networkId,
				channelDao,
				channelService,
				logger,
				search = search
			),
			pagingSourceFactory = {
				if (search.isNullOrEmpty()) channelDao.getGroupChannels(networkId, category)
				else channelDao.searchGroupChannels(networkId, search)
			}
		)
			.flow.map { data -> data.map { it.toModel() } }
	}

	override suspend fun getGroupChannel(id: String) = channelFlowWithAwait {
		launch(Dispatchers.Unconfined) {
			channelDao
				.getGroupChannel(id)
				.mapNotNull { channel -> channel?.toModel() }
				.collect { trySend(it) }
		}
		launch {
			channelService.getChannel(id, type = ChannelType.GROUP).let {
				it as ApiGroupChannel
				channelDao.upsert(it.toEntity())
			}
		}
	}

	override suspend fun getDirectChannel(id: String) = channelFlowWithAwait {
		launch(Dispatchers.Unconfined) {
			channelDao
				.getDirectChannel(id)
				.mapNotNull { channel -> channel?.toModel() }
				.collectLatest { trySend(it) }
		}
		launch {
			channelService.getChannel(id, type = ChannelType.GROUP).let {
				it as ApiDirectChannel
				channelDao.upsert(it.toEntity(userId))
			}
		}
	}

	override suspend fun createGroupChannel(networkId: String, channel: GroupChannel): GroupChannel {
		return channelService.createGroupChannel(networkId, channel).toModel()
	}

	override suspend fun createDirectChannel(members: List<Member>): DirectChannel {
		return channelService.createDirectChannel(members).toModel(userId)
	}

	override suspend fun updateChannel(channel: Channel) {
		channelService.updateChannel(channel)
	}

	override suspend fun updateNotificationSettings(channel: Channel, alertType: AlertType) {
		channelService.updateNotificationSettings(channel, alertType)
	}

	override suspend fun joinChannel(channel: Channel) = channelService.joinChannel(channel)

	override suspend fun deleteChannel(channel: Channel) {
		channelDao.delete(ChannelEntity(id = channel.id, isDirectChannel = channel is DirectChannel))
		channelService.deleteChannel(channel)
	}

	override suspend fun markChannelRead(channel: Channel) {
		channelService.markChannelRead(channel)
	}
}
