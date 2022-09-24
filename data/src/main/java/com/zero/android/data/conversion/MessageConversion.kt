package com.zero.android.data.conversion

import com.zero.android.database.model.MemberEntity
import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.models.DraftMessage
import com.zero.android.models.enums.MessageStatus

internal fun DraftMessage.toEntity(mentions: List<MemberEntity>? = null): MessageWithRefs =
	MessageWithRefs(
		message =
		MessageEntity(
			id = MessageEntity.generateDraftId(fileRequestId),
			requestId = fileRequestId,
			channelId = channelId,
			authorId = author.id,
			parentMessageId = parentMessage?.id,
			parentMessageAuthorId = parentMessage?.author?.id,
			type = type,
			mentionType = mentionType,
			createdAt = createdAt,
			updatedAt = updatedAt,
			status = MessageStatus.PENDING,
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
