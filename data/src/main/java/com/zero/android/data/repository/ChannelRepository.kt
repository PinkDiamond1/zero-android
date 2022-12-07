package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Member
import com.zero.android.models.Message
import com.zero.android.models.enums.AlertType
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ChannelRepository {

	fun getDirectChannels(search: String? = null): Flow<PagingData<DirectChannel>>

	fun getGroupChannels(
		networkId: String,
		category: String? = null,
		search: String? = null
	): Flow<PagingData<GroupChannel>>

	suspend fun getChannel(id: String): Flow<Channel>

	suspend fun createGroupChannel(networkId: String, channel: GroupChannel): GroupChannel

	suspend fun createDirectChannel(members: List<Member>): DirectChannel

	suspend fun updateChannel(channel: Channel)

	suspend fun updateChannelImage(channel: Channel, image: File)

	suspend fun addMembers(id: String, members: List<Member>)

	suspend fun updateNotificationSettings(channel: Channel, alertType: AlertType)

	suspend fun joinPublicChannels(networkId: String)

	suspend fun joinChannel(channel: Channel)

	suspend fun leaveChannel(channel: Channel)

	suspend fun deleteChannel(channel: Channel)

	suspend fun markChannelRead(channel: Channel)

	suspend fun getUnreadDirectMessagesCount(): Flow<Int>

	suspend fun getReadMembers(id: String): List<Member>

	suspend fun getLastMessage(channelId: String): Flow<Message>
}
