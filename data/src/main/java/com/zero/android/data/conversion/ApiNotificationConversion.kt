package com.zero.android.data.conversion

import com.zero.android.database.model.NotificationEntity
import com.zero.android.network.model.ApiNotification

internal fun ApiNotification.toEntity(title: String, description: String, image: String?) =
	NotificationEntity(
		id = id,
		title = title,
		description = description,
		image = image,
		userId = userId,
		type = type,
		category = category,
		isRead = isRead,
		originUserId = originUserId,
		networkId = data?.network?.id,
		channelId = data?.channelId,
		createdAt = createdAt
	)
