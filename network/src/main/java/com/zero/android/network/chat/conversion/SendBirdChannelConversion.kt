package com.zero.android.network.chat.conversion

import com.sendbird.android.BaseChannel
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelParams
import com.sendbird.android.Member
import com.sendbird.android.OpenChannel
import com.sendbird.android.OpenChannelParams
import com.zero.android.database.converter.AppJson.decodeJson
import com.zero.android.database.converter.AppJson.toJson
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.enums.AccessType
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.ChannelType
import com.zero.android.models.enums.toAlertType
import com.zero.android.models.enums.toChannelType
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.model.ApiChannelProperties
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal fun BaseChannel.toApi(): ApiChannel {
	if (this is OpenChannel) return toApi() else if (this is GroupChannel) return toApi()
	throw IllegalStateException("not handled")
}

internal fun getNetworkId(customType: String): String? {
	return Regex(pattern = "network:([-a-zA-Z0-9]+)").matchEntire(customType)?.groups?.get(1)?.value
}

internal fun String.encodeToNetworkId() =
	if (this.isNotEmpty() && getNetworkId(this).isNullOrEmpty()) "network:$this" else this

internal val OpenChannel.networkId
	get() = customType?.let { getNetworkId(it) }
internal val GroupChannel.networkId
	get() = customType?.let { getNetworkId(it) }

private val GroupChannel.isDirectChannel
	get() = networkId == null

private val BaseChannel.image
	get() = if (coverUrl.contains("static.sendbird.com/sample")) null else coverUrl

internal fun Channel.isGroupChannel() =
	this is DirectChannel ||
		(this is com.zero.android.models.GroupChannel && type == ChannelType.GROUP)

internal fun Channel.isOpenChannel() =
	this is com.zero.android.models.GroupChannel && type == ChannelType.OPEN

internal fun OpenChannel.toApi(): ApiGroupChannel {
	val properties = data?.decodeJson<ApiChannelProperties?>()
	return ApiGroupChannel(
		id = url,
		networkId = networkId ?: "",
		name = name,
		description = properties?.description,
		members = operators.map { it.toApi() },
		memberCount = participantCount,
		operators = operators.map { it.toApi() },
		createdAt = createdAt,
		image = image,
		properties = properties,
		isTemporary = isEphemeral
	)
}

internal fun GroupChannel.toApi() = if (isDirectChannel) toDirectApi() else toGroupApi()

internal fun GroupChannel.toDirectApi(): ApiDirectChannel {
	val operators = members.filter { it.role == Member.Role.OPERATOR }.map { it.toApi() }
	val properties = data?.decodeJson<ApiChannelProperties?>()
	return ApiDirectChannel(
		id = url,
		name = name,
		description = properties?.description,
		operators = operators,
		members = members.map { it.toApi() },
		memberCount = memberCount,
		image = image,
		unreadMentionCount = unreadMentionCount,
		unreadMessageCount = unreadMessageCount,
		lastMessage = lastMessage?.toApi(),
		createdAt = createdAt,
		isTemporary = isEphemeral,
		alerts = myPushTriggerOption.toType()
	)
}

internal fun GroupChannel.toGroupApi(): ApiGroupChannel {
	val operators = members.filter { it.role == Member.Role.OPERATOR }.map { it.toApi() }
	val properties = data.decodeJson<ApiChannelProperties?>()
	return ApiGroupChannel(
		id = url,
		networkId = networkId ?: "",
		category = properties?.category,
		name = name,
		description = properties?.description,
		isSuper = isSuper,
		operators = operators,
		members = members.map { it.toApi() },
		memberCount = memberCount,
		unreadMentionCount = unreadMentionCount,
		unreadMessageCount = unreadMessageCount,
		lastMessage = lastMessage?.toApi(),
		createdAt = createdAt,
		image = image,
		properties = properties,
		isTemporary = isEphemeral,
		createdBy = creator?.toApi(),
		alerts = myPushTriggerOption.toType(),
		isPublic = isPublic && isDiscoverable,
		isDiscoverable = isDiscoverable,
		isVideoEnabled = properties?.isVideoEnabled == true,
		accessType = properties?.groupChannelType ?: AccessType.PUBLIC
	)
}

internal fun DirectChannel.toParams() =
	GroupChannelParams().apply {
		setName(members.joinToString { it.name ?: "" }.trim())
		if (id.isNotEmpty()) setChannelUrl(id)
		setCoverUrl(image)
		setData(null)
		setCustomType(null)

		setSuper(false)
		setPublic(false)
		setDiscoverable(false)
		setEphemeral(isTemporary)
		setStrict(false)
		setAccessCode(null)
		setMessageSurvivalSeconds(messageLifeSeconds)

		addUserIds(members.map { it.id })
	}

internal fun toDirectParams(members: List<com.zero.android.models.Member>) =
	GroupChannelParams().apply {
		setCustomType(ChannelType.DIRECT_CHANNEL.serializedName)
		setDistinct(true)

		addUserIds(members.map { it.id })
	}

internal fun com.zero.android.models.GroupChannel.toOpenParams() =
	OpenChannelParams().apply {
		setName(name)
		if (id.isNotEmpty()) setChannelUrl(id)
		setCoverUrl(image)
		setData(Json.encodeToString(toProperties()))
		setCustomType(networkId.encodeToNetworkId())
	}

internal fun com.zero.android.models.GroupChannel.toParams() =
	GroupChannelParams().apply {
		setName(name)
		if (id.isNotEmpty()) setChannelUrl(id)
		setCoverUrl(image)
		setData(Json.encodeToString(toProperties()))
		setCustomType(networkId.encodeToNetworkId())

		setSuper(isSuper)
		setPublic(isPublic)
		setDiscoverable(isDiscoverable)
		setEphemeral(isTemporary)
		setStrict(false)
		setAccessCode(accessCode)
		setMessageSurvivalSeconds(messageLifeSeconds)

		addUserIds(members.map { it.id })
	}

internal fun DirectChannel.toUpdateParams() =
	GroupChannelParams().apply {
		setName(name)
		setCoverUrl(image)
		setCustomType(ChannelType.DIRECT_CHANNEL_NAMED.serializedName)

		setEphemeral(isTemporary)
		setAccessCode(accessCode)

		setData(toProperties().toJson())
	}

internal fun com.zero.android.models.GroupChannel.toUpdateParams() =
	GroupChannelParams().apply {
		setName(name)
		setCoverUrl(image)

		setSuper(isSuper)
		setPublic(isPublic)
		setDiscoverable(isDiscoverable)
		setEphemeral(isTemporary)
		setAccessCode(accessCode)

		setData(toProperties().toJson())
	}

private fun DirectChannel.toProperties() = ApiChannelProperties(description = description)

private fun com.zero.android.models.GroupChannel.toProperties() =
	ApiChannelProperties(
		category = category,
		isAdminOnly = isAdminOnly,
		telegramChatId = telegramChatId,
		discordChatId = discordChatId,
		isVideoEnabled = isVideoEnabled,
		groupChannelType = accessType,
		description = description
	)

internal fun BaseChannel.ChannelType.toType() = value().toChannelType()

internal fun GroupChannel.PushTriggerOption.toType() = name.lowercase().toAlertType()

internal fun AlertType.toOption() =
	GroupChannel.PushTriggerOption.values().find { it.name.lowercase() == serializedName }
