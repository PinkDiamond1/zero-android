package com.zero.android.network.service

import com.zero.android.network.model.ApiNotification
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationService {

	@GET(value = "notifications/filter")
	suspend fun getNotifications(
		@Query("userId") userId: String,
		@Query("lastCreatedAt") lastCreatedAt: String? = null,
		@Query("excludeId") excludedId: String? = null,
		@Query("filter") filterJson: String
	): List<ApiNotification>
}
