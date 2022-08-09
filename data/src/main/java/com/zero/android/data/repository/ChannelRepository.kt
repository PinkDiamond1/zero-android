package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
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

	suspend fun joinChannel(channel: Channel)

	suspend fun deleteChannel(channel: Channel)

	suspend fun markChannelRead(channel: Channel)
}
