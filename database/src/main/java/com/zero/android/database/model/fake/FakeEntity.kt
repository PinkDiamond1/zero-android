package com.zero.android.database.model.fake

import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.ChannelWithRefs
import com.zero.android.database.model.MemberEntity
import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.database.model.NetworkEntity
import com.zero.android.database.model.toModel
import com.zero.android.models.ChannelCategory
import com.zero.android.models.conversions.toMeta
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.InviteMode
import com.zero.android.models.enums.MessageStatus
import com.zero.android.models.enums.MessageType

@Suppress("TestFunctionName")
object FakeEntity {

	fun NetworkEntity(id: String = "networkId", name: String = "name") =
		NetworkEntity(
			id = id,
			name = name,
			displayName = "name",
			logo = "",
			isPublic = false,
			inviteMode = InviteMode.NONE
		)

	fun DirectChannelWithRefs(
		id: String = "directChannelId",
		name: String = "Member One, Member Two",
		lastMessage: MessageWithRefs? = MessageWithRefs(id = "directLastMessageId", channelId = id)
	) =
		ChannelWithRefs(
			channel =
			ChannelEntity(
				id = id,
				name = name,
				lastMessage = lastMessage?.toModel()?.toMeta(),
				isDirectChannel = true,
				memberCount = 2
			),
			members = listOf(MemberEntity(id = "memberOne"), MemberEntity(id = "memberTwo")),
			operators = listOf(MemberEntity(id = "memberOne"))
		)

	fun GroupChannelWithRefs(
		id: String = "groupChannelId",
		networkId: String = "networkId",
		category: ChannelCategory = "category",
		name: String = "Group Channel",
		lastMessage: MessageWithRefs? = MessageWithRefs(id = "groupLastMessageId", channelId = id)
	) =
		ChannelWithRefs(
			createdBy = MemberEntity(id = "memberFive"),
			channel =
			ChannelEntity(
				id = id,
				name = name,
				networkId = networkId,
				lastMessage = lastMessage?.toModel()?.toMeta(),
				isDirectChannel = false,
				memberCount = 2,
				category = category
			),
			members = listOf(MemberEntity(id = "memberFive"), MemberEntity(id = "memberFour")),
			operators = listOf(MemberEntity(id = "memberFive"))
		)

	fun MessageWithRefs(
		id: String = "messageId",
		channelId: String = "channelId",
		authorId: String = "memberOne",
		requestId: String? = null,
		reply: Boolean = true
	) =
		MessageWithRefs(
			message =
			MessageEntity(
				id = id,
				requestId = requestId,
				authorId = authorId,
				parentMessageId = if (reply) "parentMessageId" else null,
				parentMessageAuthorId = if (reply) "memberThree" else null,
				channelId = channelId,
				type = MessageType.TEXT,
				status = MessageStatus.PENDING,
				deliveryStatus = DeliveryStatus.SENT
			),
			author = MemberEntity(id = authorId),
			parentMessage =
			if (reply) {
				MessageEntity(
					id = "parentMessageId",
					authorId = "memberThree",
					channelId = channelId,
					type = MessageType.TEXT,
					status = MessageStatus.PENDING,
					deliveryStatus = DeliveryStatus.SENT
				)
			} else null,
			parentMessageAuthor = if (reply) MemberEntity(id = "memberThree") else null,
			mentions = listOf(MemberEntity(id = "memberOne"), MemberEntity(id = "memberTwo"))
		)
}
