package com.zero.android.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.zero.android.common.extensions.channelFlowWithAwait
import com.zero.android.common.system.Logger
import com.zero.android.common.util.CHANNELS_PAGE_LIMIT
import com.zero.android.common.util.INITIAL_LOAD_SIZE
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.conversion.toModel
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.extensions.launchSafeApi
import com.zero.android.data.mediator.DirectChannelsRemoteMediator
import com.zero.android.data.mediator.GroupChannelsRemoteMediator
import com.zero.android.database.dao.ChannelDao
import com.zero.android.database.dao.MessageDao
import com.zero.android.database.model.toDirectModel
import com.zero.android.database.model.toGroupModel
import com.zero.android.database.model.toModel
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Member
import com.zero.android.models.enums.AlertType
import com.zero.android.network.service.ChannelService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

internal class ChannelRepositoryImpl
@Inject
constructor(
	private val channelDao: ChannelDao,
	private val messageDao: MessageDao,
	private val channelService: ChannelService,
	private val fileRepository: FileRepository,
	private val logger: Logger,
	preferences: Preferences
) : ChannelRepository {

	private val userId = runBlocking { preferences.userId() }

	@OptIn(ExperimentalPagingApi::class)
	override fun getDirectChannels(search: String?): Flow<PagingData<DirectChannel>> {
		return Pager(
			config =
			PagingConfig(
				pageSize = CHANNELS_PAGE_LIMIT,
				initialLoadSize = CHANNELS_PAGE_LIMIT * INITIAL_LOAD_SIZE
			),
			remoteMediator =
			DirectChannelsRemoteMediator(userId, channelDao, channelService, logger),
			pagingSourceFactory = {
				if (search.isNullOrEmpty()) channelDao.getDirectChannels()
				else channelDao.searchDirectChannels(search)
			}
		)
			.flow
			.map { data -> data.map { it.toDirectModel() } }
	}

	@OptIn(ExperimentalPagingApi::class)
	override fun getGroupChannels(
		networkId: String,
		category: String?,
		search: String?
	): Flow<PagingData<GroupChannel>> {
		return Pager(
			config =
			PagingConfig(
				pageSize = CHANNELS_PAGE_LIMIT,
				initialLoadSize = CHANNELS_PAGE_LIMIT * INITIAL_LOAD_SIZE
			),
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
			.flow
			.map { data -> data.map { it.toGroupModel() } }
	}

	override suspend fun getChannel(id: String) = channelFlowWithAwait {
		launch(Dispatchers.Unconfined) {
			channelDao
				.getChannel(id)
				.mapNotNull { channel ->
					if (channel?.channel?.isDirectChannel == true) channel.toDirectModel()
					else channel?.toGroupModel()
				}
				.collect { trySend(it) }
		}
		launchSafeApi { channelService.getChannel(id).let { channelDao.upsert(it.toEntity(userId)) } }
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

	override suspend fun updateChannelImage(channel: Channel, image: File) {
		val file = fileRepository.upload(image)
		val mChannel =
			when (channel) {
				is DirectChannel -> channel.copy(image = file.url)
				is GroupChannel -> channel.copy(image = file.url)
				else -> throw IllegalStateException()
			}
		updateChannel(mChannel)
	}

	override suspend fun addMembers(id: String, members: List<Member>) {
		channelService.addMembers(id, members.map { it.id })
	}

	override suspend fun updateNotificationSettings(channel: Channel, alertType: AlertType) {
		channelService.updateNotificationSettings(channel.id, alertType)
	}

	override suspend fun joinPublicChannels(networkId: String) {
		val channels = channelService.getPublicChannels(networkId)
		for (channel in channels) runCatching {
			if (channel.members.find { it.id == userId } == null) {
				joinChannel(channel.toModel())
			}
		}
	}

	override suspend fun joinChannel(channel: Channel) = channelService.joinChannel(channel)

	override suspend fun leaveChannel(channel: Channel) {
		channelService.leaveChannel(channel)
		channelDao.delete(channel.id)
	}

	override suspend fun deleteChannel(channel: Channel) {
		channelDao.delete(channel.id)
		channelService.deleteChannel(channel)
	}

	override suspend fun markChannelRead(channel: Channel) {
		channelService.markChannelRead(channel)
	}

	override suspend fun getReadMembers(id: String): List<Member> {
		return channelService.getReadMembers(id).map { it.toModel() }
	}

	override suspend fun getUnreadDirectMessagesCount() = channelDao.getUnreadDirectMessagesCount()

	override suspend fun getLastMessage(channelId: String) =
		messageDao.getLastMessage(channelId).map { it.toModel() }
}
