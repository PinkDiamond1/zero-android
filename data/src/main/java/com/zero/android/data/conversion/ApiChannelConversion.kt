package com.zero.android.data.conversion

import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.ChannelWithRefs
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.conversions.toMeta
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel

internal fun ApiChannel.toEntity(loggedInUserId: String?): ChannelWithRefs {
	return if (this is ApiDirectChannel) toEntity(loggedInUserId)
	else (this as ApiGroupChannel).toEntity()
}

internal fun ApiDirectChannel.toModel(loggedInUserId: String?) =
	DirectChannel(
		id = id,
		name = name(loggedInUserId),
		description = description,
		operators = operators.map { it.toModel() },
		members = members.map { it.toModel() },
		memberCount = memberCount,
		image = image(loggedInUserId),
		lastMessage = lastMessage?.toModel()?.toMeta(),
		createdAt = createdAt,
		isTemporary = isTemporary,
		unreadMentionCount = unreadMentionCount,
		alerts = alerts,
		accessCode = accessCode
	)

internal fun ApiDirectChannel.toEntity(loggedInUserId: String?) =
	ChannelWithRefs(
		channel =
		ChannelEntity(
			id = id,
			name = name(loggedInUserId),
			description = description,
			lastMessage = lastMessage?.toModel()?.toMeta(),
			lastMessageTime = lastMessage?.createdAt ?: 0L,
			isDirectChannel = true,
			memberCount = memberCount,
			image = image(loggedInUserId),
			createdAt = createdAt,
			isTemporary = isTemporary,
			unreadMentionCount = unreadMentionCount,
			unreadMessageCount = unreadMessageCount,
			alerts = alerts,
			accessCode = accessCode
		),
		operators = operators.map { it.toEntity() },
		members = members.map { it.toEntity() }
	)

fun ApiGroupChannel.toModel() =
	GroupChannel(
		id = id,
		networkId = networkId,
		category = category,
		name = name,
		description = description,
		isSuper = isSuper,
		operators = operators.map { it.toModel() },
		members = members.map { it.toModel() },
		memberCount = memberCount,
		unreadMentionCount = unreadMentionCount,
		unreadMessageCount = unreadMessageCount,
		lastMessage = lastMessage?.toModel()?.toMeta(),
		createdAt = createdAt,
		createdBy = createdBy?.toModel(),
		image = image,
		isAdminOnly = properties?.isAdminOnly ?: false,
		telegramChatId = properties?.telegramChatId,
		discordChatId = properties?.discordChatId ?: properties?.discordChannelId,
		isTemporary = isTemporary,
		alerts = alerts,
		isPublic = isPublic,
		isDiscoverable = isDiscoverable,
		accessCode = accessCode,
		messageLifeSeconds = messageLifeSeconds,
		type = type,
		accessType = accessType,
		isVideoEnabled = isVideoEnabled
	)

internal fun ApiGroupChannel.toEntity() =
	ChannelWithRefs(
		channel =
		ChannelEntity(
			id = id,
			lastMessage = lastMessage?.toModel()?.toMeta(),
			lastMessageTime = lastMessage?.createdAt ?: 0L,
			authorId = createdBy?.id,
			isDirectChannel = false,
			memberCount = memberCount,
			image = image,
			createdAt = createdAt,
			isTemporary = isTemporary,
			unreadMentionCount = unreadMentionCount,
			unreadMessageCount = unreadMessageCount,
			messageLifeSeconds = messageLifeSeconds,
			alerts = alerts,
			accessCode = accessCode,
			networkId = networkId,
			category = category,
			name = name,
			description = description,
			isSuper = isSuper,
			isPublic = isPublic,
			isDiscoverable = isDiscoverable,
			isVideoEnabled = isVideoEnabled,
			type = type,
			isAdminOnly = properties?.isAdminOnly ?: false,
			telegramChatId = properties?.telegramChatId,
			discordChatId = properties?.discordChatId ?: properties?.discordChannelId,
			accessType = accessType
		),
		members = members.map { it.toEntity() },
		operators = operators.map { it.toEntity() },
		createdBy = createdBy?.toEntity()
	)
