package com.zero.android.models

import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.MessageMentionType
import com.zero.android.models.enums.MessageStatus
import com.zero.android.models.enums.MessageType
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*

internal interface BaseMessage {
	val channelId: String
	val author: BaseMember?
	val type: MessageType
	val mentionType: MessageMentionType
	val message: String?
	val createdAt: Long
	val updatedAt: Long
	val status: MessageStatus
	val deliveryStatus: DeliveryStatus
	val data: String?
	val isMuted: Boolean
	val fileName: String?
	val fileThumbnails: List<FileThumbnail>?
	val fileMimeType: String?
}

data class Message(
	val id: String,
	override val channelId: String,
	override val author: Member? = null,
	val mentions: List<Member> = emptyList(),
	override val type: MessageType,
	override val mentionType: MessageMentionType = MessageMentionType.UNKNOWN,
	override val message: String? = null,
	override val createdAt: Long,
	override val updatedAt: Long,
	override val status: MessageStatus,
	override val deliveryStatus: DeliveryStatus,
	override val data: String? = null,
	val parentMessage: Message? = null,
	override val isMuted: Boolean = false,
	val fileUrl: String? = null,
	override val fileName: String? = null,
	override val fileThumbnails: List<FileThumbnail>? = null,
	override val fileMimeType: String? = null,
	val reactions: List<MessageReaction> = emptyList()
) : BaseMessage {

	val isReply
		get() = parentMessage != null

	val isDraft
		get() = status == MessageStatus.DRAFT

	val isSent
		get() = status != MessageStatus.DRAFT && !id.startsWith(DraftMessage.PREFIX_DRAFT_ID)
}

data class DraftMessage(
	override val channelId: String,
	override val author: Member,
	val mentions: List<String> = emptyList(),
	override val type: MessageType,
	override val mentionType: MessageMentionType = MessageMentionType.UNKNOWN,
	override val message: String? = null,
	override val createdAt: Long,
	override val updatedAt: Long,
	override val status: MessageStatus = MessageStatus.NONE,
	override val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT,
	override val data: String? = null,
	var parentMessage: Message? = null,
	override val isMuted: Boolean = false,
	val file: File? = null,
	val fileUrl: String? = null,
	val fileRequestId: String? = if (file != null) UUID.randomUUID().toString() else null,
	override val fileName: String? = null,
	override val fileThumbnails: List<FileThumbnail>? = null,
	override val fileMimeType: String? = null
) : BaseMessage {

	companion object {
		const val PREFIX_DRAFT_ID = "draft_"
	}
}

@Serializable
data class MessageMeta(
	val id: String,
	override val channelId: String,
	override val author: MemberMeta?,
	override val type: MessageType,
	override val mentionType: MessageMentionType,
	override val message: String?,
	override val createdAt: Long,
	override val updatedAt: Long,
	override val status: MessageStatus,
	override val deliveryStatus: DeliveryStatus,
	override val data: String? = null,
	override val isMuted: Boolean = false,
	override val fileName: String? = null,
	override val fileThumbnails: List<FileThumbnail>? = null,
	override val fileMimeType: String? = null
) : BaseMessage

@Serializable
data class FileThumbnail(
	var maxWidth: Int = 0,
	val maxHeight: Int = 0,
	val realWidth: Int = 0,
	val realHeight: Int = 0,
	val url: String? = null
)
