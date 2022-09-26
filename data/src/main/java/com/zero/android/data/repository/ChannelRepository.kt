package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Member
import com.zero.android.models.enums.AlertType
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {

	fun getDirectChannels(search: String? = null): Flow<PagingData<DirectChannel>>

	fun getGroupChannels(
		networkId: String,
		category: String? = null,
		search: String? = null
	): Flow<PagingData<GroupChannel>>

	suspend fun getGroupChannel(id: String): Flow<GroupChannel>

	suspend fun getDirectChannel(id: String): Flow<DirectChannel>

	suspend fun createGroupChannel(networkId: String, channel: GroupChannel): GroupChannel

	suspend fun createDirectChannel(members: List<Member>): DirectChannel

	suspend fun updateChannel(channel: Channel)

	suspend fun updateNotificationSettings(channel: Channel, alertType: AlertType)

	suspend fun joinChannel(channel: Channel)

	suspend fun deleteChannel(channel: Channel)

	suspend fun markRead(channel: Channel)

	suspend fun getUnreadDirectMessagesCount(): Flow<Int>
}
