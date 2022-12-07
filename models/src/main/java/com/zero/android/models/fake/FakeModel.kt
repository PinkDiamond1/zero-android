package com.zero.android.models.fake

import com.zero.android.models.ChannelCategory
import com.zero.android.models.ChatMedia
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Member
import com.zero.android.models.Message
import com.zero.android.models.Network
import com.zero.android.models.Notification
import com.zero.android.models.enums.ConnectionStatus
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.MessageStatus
import com.zero.android.models.enums.MessageType
import com.zero.android.models.enums.NotificationType
import com.zero.android.models.enums.toNotificationCategory
import kotlinx.datetime.Instant

object FakeModel {

	fun Network(id: String = "id") =
		Network(
			id = id,
			name = "display.name",
			displayName = "Display Name",
			isPublic = true,
			unreadCount = 4
		)

	fun networks() = listOf(Network("one"), Network("two"), Network("three"))

	fun Member(
		id: String = "id",
		name: String = "Member Name",
		status: ConnectionStatus = ConnectionStatus.OFFLINE
	) = Member(id = id, name = name, status = status, isActive = true)

	fun members() = listOf(Member("one"), Member("two"))

	fun GroupChannel(
		id: String = "id",
		name: String = "Group Name",
		network: String = "networkId",
		category: ChannelCategory? = null
	) =
		GroupChannel(
			id = id,
			networkId = network,
			category = category,
			name = name,
			description = "Group description",
			members = members(),
			operators = listOf(Member("one")),
			memberCount = 2,
			createdAt = 0L
		)

	fun DirectChannel(id: String = "id", name: String = "Group Name") =
		DirectChannel(
			id = id,
			name = name,
			members = members(),
			operators = listOf(Member("one")),
			memberCount = 2,
			createdAt = 0L
		)

	fun Message(
		id: String = "id",
		channelId: String = "id",
		status: MessageStatus = MessageStatus.SUCCEEDED,
		deliveryStatus: DeliveryStatus = DeliveryStatus.SENT
	) =
		Message(
			id = id,
			channelId = channelId,
			author = Member(id = "authorId"),
			createdAt = 0,
			updatedAt = 0,
			status = status,
			deliveryStatus = deliveryStatus,
			type = MessageType.TEXT
		)

	fun DraftMessage(channelId: String = "id", status: MessageStatus = MessageStatus.SUCCEEDED) =
		com.zero.android.models.DraftMessage(
			channelId = channelId,
			author = Member(id = "authorId"),
			createdAt = 0,
			updatedAt = 0,
			status = status,
			type = MessageType.TEXT
		)

	fun media() =
		listOf(
			ChatMedia("id", "", MessageType.IMAGE),
			ChatMedia("id", "", MessageType.IMAGE),
			ChatMedia("id", "", MessageType.AUDIO),
			ChatMedia("id", "", MessageType.IMAGE)
		)

	fun Notification(
		id: String = "id",
		title: String = "Notification Title",
		description: String = "New reply in direct message conversation",
		type: NotificationType = NotificationType.DM_REPLY
	) =
		Notification(
			id = id,
			title = title,
			description = description,
			type = type,
			category = type.toNotificationCategory(),
			isRead = true,
			userId = "userId",
			originUserId = "originUserId",
			channelId = "channelId",
			createdAt = Instant.DISTANT_FUTURE
		)
}
