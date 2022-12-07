package com.zero.android.models

import com.zero.android.models.enums.NotificationCategory
import com.zero.android.models.enums.NotificationType
import kotlinx.datetime.Instant

data class Notification(
	val id: String,
	val title: String? = null,
	val description: String,
	val image: String? = null,
	val userId: String? = null,
	val category: NotificationCategory,
	val type: NotificationType,
	val isRead: Boolean = false,
	val createdAt: Instant,
	val originUserId: String? = null,
	val networkId: String? = null,
	val channelId: String? = null
)
