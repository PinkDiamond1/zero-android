package com.zero.android.network.service

import com.zero.android.models.Channel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Member
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.ChannelType
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel
import com.zero.android.network.model.ApiMember

interface ChannelService {

	suspend fun getGroupChannels(
		networkId: String,
		type: ChannelType = ChannelType.GROUP,
		before: String? = null,
		loadSize: Int = 1,
		limit: Int = 100,
		searchName: String? = null,
		refresh: Boolean = false
	): List<ApiGroupChannel>

	suspend fun getDirectChannels(
		before: String? = null,
		loadSize: Int = 1,
		searchName: String? = null,
		refresh: Boolean = false
	): List<ApiDirectChannel>

	suspend fun getPublicChannels(networkId: String): List<ApiGroupChannel>

	suspend fun getChannel(url: String, type: ChannelType = ChannelType.GROUP): ApiChannel

	suspend fun createGroupChannel(networkId: String, channel: GroupChannel): ApiGroupChannel

	suspend fun createDirectChannel(members: List<Member>): ApiDirectChannel

	suspend fun updateChannel(channel: Channel): ApiChannel

	suspend fun getNetworkNotificationSettings(networkId: String): AlertType

	suspend fun updateNotificationSettings(channel: Channel, alertType: AlertType)

	suspend fun updateNotificationSettings(networkId: String, alertType: AlertType)

	suspend fun joinChannel(channel: Channel)

	suspend fun leaveChannel(channel: Channel)

	suspend fun deleteChannel(channel: Channel)

	suspend fun markChannelRead(channel: Channel)

	suspend fun getReadMembers(id: String): List<ApiMember>
}
