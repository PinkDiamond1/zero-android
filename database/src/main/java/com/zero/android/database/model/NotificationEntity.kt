package com.zero.android.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zero.android.models.Notification
import com.zero.android.models.enums.NotificationCategory
import com.zero.android.models.enums.NotificationType
import kotlinx.datetime.Instant

@Entity(tableName = "notifications")
data class NotificationEntity(
	@PrimaryKey override val id: String,
	val title: String? = null,
	val description: String = "",
	val image: String? = null,
	val userId: String? = null,
	val category: NotificationCategory = NotificationCategory.NONE,
	val type: NotificationType = NotificationType.NONE,
	val isRead: Boolean = false,
	val createdAt: Instant,
	val originUserId: String? = null,
	val networkId: String? = null,
	val channelId: String? = null
) : BaseEntity

fun NotificationEntity.toModel() =
	Notification(
		id = id,
		title = title,
		description = description,
		image = image,
		userId = userId,
		type = type,
		category = category,
		isRead = isRead,
		originUserId = originUserId,
		networkId = networkId,
		channelId = channelId,
		createdAt = createdAt
	)
