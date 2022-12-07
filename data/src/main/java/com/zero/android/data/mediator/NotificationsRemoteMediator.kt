package com.zero.android.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.zero.android.common.system.Logger
import com.zero.android.data.extensions.initialPageSize
import com.zero.android.data.formatter.NotificationParser
import com.zero.android.database.dao.NotificationDao
import com.zero.android.database.model.NotificationEntity
import com.zero.android.network.model.request.GetNotificationsFilter
import com.zero.android.network.service.NotificationService
import java.io.IOException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
internal class NotificationsRemoteMediator(
	private val userId: String,
	private val notificationDao: NotificationDao,
	private val notificationService: NotificationService,
	private val notificationParser: NotificationParser,
	private val logger: Logger
) : RemoteMediator<Int, NotificationEntity>() {

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, NotificationEntity>
	): MediatorResult {
		return try {
			val lastNotification =
				when (loadType) {
					LoadType.REFRESH -> null
					LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
					LoadType.APPEND -> {
						state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
					}
				}

			val response =
				notificationService.getNotifications(
					userId = userId,
					lastCreatedAt = lastNotification?.createdAt?.toString(),
					excludedId = lastNotification?.id,
					GetNotificationsFilter(
						limit =
						if (lastNotification == null) state.config.initialPageSize
						else state.config.pageSize
					)
						.toString()
				)

			response.map { notificationParser.parse(it) }.let { notificationDao.upsert(it) }

			logger.d("Loading Notifications: $loadType - ${lastNotification?.id}: ${response.size}")

			MediatorResult.Success(
				endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
			)
		} catch (e: UnknownHostException) {
			MediatorResult.Error(e)
		} catch (e: IOException) {
			MediatorResult.Error(e)
		} catch (e: Exception) {
			MediatorResult.Error(e)
		}
	}

	override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH
}
