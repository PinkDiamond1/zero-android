package com.zero.android.models

import com.zero.android.common.R
import com.zero.android.models.enums.AccessType
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.ChannelType

interface Channel {
	val id: String
	val name: String
	val members: List<Member>
	val memberCount: Int
	val image: String?
	val createdAt: Long
	val isTemporary: Boolean
	val unreadMentionCount: Int
	val unreadMessageCount: Int
	val lastMessage: Message?
	val messageLifeSeconds: Int
	val alerts: AlertType
	val accessCode: String?
}

typealias ChannelCategory = String

data class DirectChannel(
	override val id: String,
	override val name: String,
	override val members: List<Member>,
	override val memberCount: Int,
	override val image: String? = null,
	override val lastMessage: Message? = null,
	override val createdAt: Long,
	override val isTemporary: Boolean = false,
	override val unreadMentionCount: Int = 0,
	override val unreadMessageCount: Int = 0,
	override val messageLifeSeconds: Int = 0,
	override val alerts: AlertType = AlertType.DEFAULT,
	override val accessCode: String? = null
) : Channel

data class GroupChannel(
	override val id: String,
	val networkId: String,
	val category: ChannelCategory? = null,
	override val name: String,
	val operators: List<Member>,
	override val members: List<Member>,
	override val memberCount: Int,
	override val image: String? = null,
	override val lastMessage: Message? = null,
	override val createdAt: Long,
	override val isTemporary: Boolean = false,
	val isSuper: Boolean = false,
	val isPublic: Boolean = false,
	val isDiscoverable: Boolean = false,
	val isVideoEnabled: Boolean = false,
	val createdBy: Member? = null,
	override val alerts: AlertType = AlertType.DEFAULT,
	override val messageLifeSeconds: Int = 0,
	override val accessCode: String? = null,
	val type: ChannelType = ChannelType.GROUP,
	override val unreadMentionCount: Int = 0,
	override val unreadMessageCount: Int = 0,
	val isAdminOnly: Boolean = false,
	val telegramChatId: String? = null,
	val discordChatId: String? = null,
	val accessType: AccessType = AccessType.PUBLIC
) : Channel {

	val isDiscordChannel: Boolean
		get() = !discordChatId.isNullOrEmpty()

	val isTelegramChannel: Boolean
		get() = !telegramChatId.isNullOrEmpty()

	val icon: Int?
		get() = run {
			if (isTelegramChannel) R.drawable.ic_chat_icon
			else if (isDiscordChannel) R.drawable.ic_discord else null
		}
}

val Channel.isGroupChannel
	get() = this is GroupChannel
