package com.zero.android.network.model

import com.zero.android.models.enums.NotificationType
import com.zero.android.models.enums.toNotificationCategory
import com.zero.android.network.model.serializer.InstantSerializer
import com.zero.android.network.model.serializer.NotificationTypeSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiNotification(
	val id: String,
	val userId: String? = null,
	@Serializable(NotificationTypeSerializer::class)
	@SerialName("notificationType")
	val type: NotificationType,
	val isRead: Boolean = false,
	val data: ApiNotificationData? = null,
	val originUserId: String? = null,
	val originUser: ApiNetworkMember? = null,
	@Serializable(InstantSerializer::class) val createdAt: Instant
) {
	val category = type.toNotificationCategory()
}

@Serializable
data class ApiNotificationData(
	val network: ApiNetwork? = null,
	@SerialName("chatId") private val chatId: String? = null,
	@SerialName("externalId") private val _channelId: String? = null
) {
	val channelId
		get() = _channelId ?: chatId
}
