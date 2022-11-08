package com.zero.android.data.conversion

import com.zero.android.database.model.MemberEntity
import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.models.DraftMessage
import com.zero.android.models.Message
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.MessageStatus
import java.io.File

internal fun Message.toDraft() =
	DraftMessage(
		channelId = channelId,
		author = author!!,
		mentions = mentions.map { it.id },
		type = type,
		mentionType = mentionType,
		message = message,
		createdAt = createdAt,
		updatedAt = updatedAt,
		status = status,
		deliveryStatus = deliveryStatus,
		data = data,
		parentMessage = parentMessage,
		isMuted = isMuted,
		file = fileUrl?.let { File(it) },
		fileUrl = fileUrl,
		fileName = fileName,
		fileThumbnails = fileThumbnails,
		fileMimeType = fileMimeType
	)

internal fun DraftMessage.toEntity(
	requestId: String,
	mentions: List<MemberEntity>? = null
): MessageWithRefs =
	MessageWithRefs(
		message =
		MessageEntity(
			id = MessageEntity.generateDraftId(requestId),
			requestId = requestId,
			channelId = channelId,
			authorId = author.id,
			parentMessageId = parentMessage?.id,
			parentMessageAuthorId = parentMessage?.author?.id,
			type = type,
			mentionType = mentionType,
			createdAt = createdAt,
			updatedAt = updatedAt,
			status = MessageStatus.NONE,
			deliveryStatus = DeliveryStatus.SENT,
			data = data,
			message = message,
			isMuted = isMuted,
			fileUrl = fileUrl,
			fileName = fileName,
			fileThumbnails = fileThumbnails,
			fileMimeType = fileMimeType
		),
		author = author.toEntity(),
		mentions = mentions,
		parentMessage = null,
		parentMessageAuthor = null
	)
