package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
	suspend fun getNotifications(): Flow<PagingData<Notification>>
}
