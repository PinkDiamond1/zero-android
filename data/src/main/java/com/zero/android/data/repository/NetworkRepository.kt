package com.zero.android.data.repository

import com.zero.android.models.ChannelCategory
import com.zero.android.models.Network
import com.zero.android.models.enums.AlertType
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {

	suspend fun getNetworks(): Flow<List<Network>>

	suspend fun getCategories(networkId: String): Flow<List<ChannelCategory>>

	suspend fun updateNotificationSettings(networkId: String, alertType: AlertType)
}
