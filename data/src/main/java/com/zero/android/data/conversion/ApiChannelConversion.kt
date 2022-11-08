package com.zero.android.data.conversion

import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.DirectChannelWithRefs
import com.zero.android.database.model.GroupChannelWithRefs
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel

internal fun ApiDirectChannel.toModel(loggedInUserId: String?) =
	DirectChannel(
		id = id,
		name = name(loggedInUserId),
		members = members.map { it.toModel() },
		memberCount = memberCount,
		image = image(loggedInUserId),
		lastMessage = lastMessage?.toModel(),
		createdAt = createdAt,
		isTemporary = isTemporary,
		unreadMentionCount = unreadMentionCount,
		alerts = alerts,
		accessCode = accessCode
	)

internal fun ApiDirectChannel.toEntity(loggedInUserId: String?) =
	DirectChannelWithRefs(
		channel =
		ChannelEntity(
			id = id,
			name = name(loggedInUserId),
			lastMessageId = lastMessage?.id,
			isDirectChannel = true,
			memberCount = memberCount,
			image = image(loggedInUserId),
			createdAt = createdAt,
			lastMessageTime = lastMessage?.createdAt ?: 0,
			isTemporary = isTemporary,
			unreadMentionCount = unreadMentionCount,
			unreadMessageCount = unreadMessageCount,
			alerts = alerts,
			accessCode = accessCode
		),
		members = members.map { it.toEntity() },
		lastMessage = lastMessage?.toEntity()
	)

internal fun ApiGroupChannel.toModel() =
	GroupChannel(
		id = id,
		networkId = networkId,
		category = category,
		name = name,
		isSuper = isSuper,
		operators = operators.map { it.toModel() },
		members = members.map { it.toModel() },
		memberCount = memberCount,
		unreadMentionCount = unreadMentionCount,
		unreadMessageCount = unreadMessageCount,
		lastMessage = lastMessage?.toModel(),
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
	GroupChannelWithRefs(
		channel =
		ChannelEntity(
			id = id,
			lastMessageId = lastMessage?.id,
			authorId = createdBy?.id ?: "",
			isDirectChannel = false,
			memberCount = memberCount,
			image = image,
			createdAt = createdAt,
			lastMessageTime = lastMessage?.createdAt ?: 0,
			isTemporary = isTemporary,
			unreadMentionCount = unreadMentionCount,
			unreadMessageCount = unreadMessageCount,
			messageLifeSeconds = messageLifeSeconds,
			alerts = alerts,
			accessCode = accessCode,
			networkId = networkId,
			category = category,
			name = name,
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
		lastMessage = lastMessage?.toEntity(),
		createdBy = createdBy?.toEntity()
	)
