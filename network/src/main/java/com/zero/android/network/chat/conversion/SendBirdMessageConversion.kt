package com.zero.android.network.chat.conversion

import com.sendbird.android.BaseChannel.ChannelType.GROUP
import com.sendbird.android.BaseMessage
import com.sendbird.android.BaseMessageParams
import com.sendbird.android.FileMessage
import com.sendbird.android.FileMessageParams
import com.sendbird.android.MessageMetaArray
import com.sendbird.android.MessageRetrievalParams
import com.sendbird.android.ReactionEvent
import com.sendbird.android.ReactionEvent.ReactionEventAction
import com.sendbird.android.UserMessage
import com.sendbird.android.UserMessageParams
import com.sendbird.android.constant.StringSet.value
import com.zero.android.models.DraftMessage
import com.zero.android.models.FileThumbnail
import com.zero.android.models.enums.MessageMentionType
import com.zero.android.models.enums.MessageStatus
import com.zero.android.models.enums.MessageType
import com.zero.android.models.enums.toMessageMentionType
import com.zero.android.models.enums.toMessageReactionAction
import com.zero.android.models.enums.toMessageStatus
import com.zero.android.models.enums.toMessageType
import com.zero.android.network.model.ApiFileData
import com.zero.android.network.model.ApiFileThumbnail
import com.zero.android.network.model.ApiMessage
import com.zero.android.network.model.ApiMessageReaction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val KEY_FILE_REQUEST_META = "fileRequest"

private fun BaseMessage.requestId(): String? {
	return if (this !is FileMessage) requestId
	else {
		getMetaArrays(listOf(KEY_FILE_REQUEST_META))?.takeIf { it.isNotEmpty() }?.let { it[0].value[0] }
			?: requestId
	}
}

internal fun BaseMessage.toApi(
	channelId: String = channelUrl,
	mParentMessageId: Long? = null
): ApiMessage {
	val data =
		if (this is FileMessage && data.isNotEmpty()) {
			Json { ignoreUnknownKeys = true }.decodeFromString<ApiFileData?>(data)
		} else null
	return ApiMessage(
		id = mParentMessageId?.toString() ?: messageId.toString(),
		type = if (this is FileMessage) data?.type.toMessageType() else customType.toMessageType(),
		mentionType = mentionType.toType(),
		channelId = channelId,
		author = sender?.toApi(),
		status = sendingStatus.toType(),
		createdAt = createdAt,
		updatedAt = updatedAt,
		message = message,
		parentMessage = parentMessage?.toApi(channelId, parentMessageId),
		fileUrl = (this as? FileMessage)?.url,
		fileName = (this as? FileMessage)?.name,
		requestId = requestId()
	)
}

internal fun UserMessage.toApi(channelId: String = channelUrl) =
	ApiMessage(
		id = messageId.toString(),
		channelId = channelId,
		author = sender?.toApi(),
		mentions = mentionedUsers.map { it.toApi() },
		type = customType.toMessageType(),
		mentionType = mentionType.toType(),
		createdAt = createdAt,
		updatedAt = updatedAt,
		status = sendingStatus.toType(),
		data = data,
		parentMessage = parentMessage?.toApi(channelId, parentMessageId),
		isMuted = isSilent,
		message = message,
		requestId = requestId
	)

internal fun FileMessage.toApi(channelId: String = channelUrl) =
	ApiMessage(
		id = messageId.toString(),
		channelId = channelId,
		author = sender?.toApi(),
		mentions = mentionedUsers.map { it.toApi() },
		type = type.toMessageType(),
		mentionType = mentionType.toType(),
		createdAt = createdAt,
		updatedAt = updatedAt,
		status = sendingStatus.toType(),
		data = data,
		parentMessage = parentMessage?.toApi(channelId, parentMessageId),
		isMuted = isSilent,
		fileUrl = url,
		fileName = name,
		fileThumbnails = thumbnails.map { it.toApi() },
		fileMimeType = messageParams?.mimeType,
		requestId = requestId()
	)

internal fun FileMessage.Thumbnail.toApi() =
	ApiFileThumbnail(
		maxWidth = maxWidth,
		maxHeight = maxHeight,
		realWidth = realWidth,
		realHeight = realHeight,
		url = url
	)

internal fun ApiMessage.toParams() = MessageRetrievalParams(channelId, GROUP, id.toLong())

internal fun DraftMessage.toParams(): BaseMessageParams {
	return if (type == MessageType.TEXT) {
		UserMessageParams().also { params ->
			params.message = message!!
			params.data = data
			parentMessage?.let { params.parentMessageId = it.id.toLong() }
			params.customType = type.serializedName
			params.mentionedUserIds = mentions
		}
	} else {
		FileMessageParams().also { params ->
			params.data = data
			parentMessage?.let { params.parentMessageId = it.id.toLong() }
			params.customType = type.serializedName
			params.mentionedUserIds = mentions

			params.file = file
			params.fileUrl = fileUrl
			params.fileName = fileName
			params.thumbnailSizes = fileThumbnails?.map { it.toSize() }
			params.mimeType = fileMimeType

			fileRequestId?.let {
				params.metaArrays = listOf(MessageMetaArray(KEY_FILE_REQUEST_META, listOf(it)))
			}
		}
	}
}

internal fun ReactionEvent.toApi() =
	ApiMessageReaction(messageId = messageId, key = key, userId = userId, updatedAt = updatedAt)

internal fun FileThumbnail.toSize() = FileMessage.ThumbnailSize(maxWidth, maxHeight)

internal fun BaseMessageParams.MentionType.toType() = value.toMessageMentionType()

internal fun MessageMentionType.toType() = BaseMessageParams.MentionType.valueOf(serializedName)

internal fun BaseMessage.SendingStatus.toType() = value.toMessageStatus()

internal fun MessageStatus.toStatus() = MessageStatus.valueOf(serializedName)

internal fun ReactionEventAction.toType() = value.lowercase().toMessageReactionAction()
