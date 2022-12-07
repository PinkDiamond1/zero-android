package com.zero.android.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.zero.android.common.system.Logger
import com.zero.android.common.util.NOTIFICATION_PAGE_LIMIT
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.formatter.NotificationParser
import com.zero.android.data.mediator.NotificationsRemoteMediator
import com.zero.android.database.dao.NotificationDao
import com.zero.android.database.model.toModel
import com.zero.android.models.Notification
import com.zero.android.network.service.NotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class NotificationRepositoryImpl
@Inject
constructor(
	preferences: Preferences,
	private val notificationDao: NotificationDao,
	private val notificationService: NotificationService,
	private val notificationParser: NotificationParser,
	private val logger: Logger
) : NotificationRepository {

	private val userId = runBlocking { preferences.userId() }

	@OptIn(ExperimentalPagingApi::class)
	override suspend fun getNotifications(): Flow<PagingData<Notification>> {
		return Pager(
			config =
			PagingConfig(
				pageSize = NOTIFICATION_PAGE_LIMIT,
				initialLoadSize = NOTIFICATION_PAGE_LIMIT
			),
			remoteMediator =
			NotificationsRemoteMediator(
				userId,
				notificationDao,
				notificationService,
				notificationParser,
				logger
			),
			pagingSourceFactory = { notificationDao.getAll() }
		)
			.flow
			.map { data -> data.map { it.toModel() } }
	}
}
