package com.zero.android.network.service

import com.zero.android.models.Channel
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.ChannelType
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel
import kotlinx.coroutines.flow.Flow

interface ChannelService {

	suspend fun getGroupChannels(
		networkId: String,
		type: ChannelType = ChannelType.GROUP,
		before: String? = null,
		loadSize: Int = 1,
		searchName: String? = null
	): Flow<List<ApiGroupChannel>>

	suspend fun getDirectChannels(
		before: String? = null,
		loadSize: Int = 1,
		searchName: String? = null
	): Flow<List<ApiDirectChannel>>

	suspend fun getChannel(url: String, type: ChannelType = ChannelType.GROUP): ApiChannel

	suspend fun createChannel(networkId: String, channel: Channel): ApiChannel

	suspend fun updateChannel(channel: Channel): ApiChannel

	suspend fun updateNotificationSettings(channel: Channel, alertType: AlertType)

	suspend fun joinChannel(channel: Channel)

	suspend fun deleteChannel(channel: Channel)

	suspend fun markChannelRead(channel: Channel)
}
