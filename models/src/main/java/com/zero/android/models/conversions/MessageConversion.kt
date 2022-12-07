package com.zero.android.models.conversions

import com.zero.android.models.Message
import com.zero.android.models.MessageMeta

fun Message.toMeta() =
	MessageMeta(
		id = id,
		author = author?.toMeta(),
		channelId = channelId,
		type = type,
		mentionType = mentionType,
		message = message,
		createdAt = createdAt,
		updatedAt = updatedAt,
		status = status,
		deliveryStatus = deliveryStatus
	)
