package com.zero.android.models.enums

enum class NotificationType(val serializedName: String) {
	NONE(""),
	FEED_COMMENT_MENTION("feed_item_comment_mention"),
	FEED_COMMENT_REPLY("feed_item_comment_replied"),
	COMMENT_ADDED_PARTICIPANT("comment_added_participant"),
	COMMENT_ADDED_OWNER("comment_added_owner"),
	TASK_ASSIGNED("task_item_assigned"),
	TASK_COMMENT_MENTION("task_item_comment_mention"),
	TASK_COMMENT_ASSIGNED("task_item_comment_assigned"),
	TASK_COMMENT_CREATOR("task_item_comment_creator"),
	TASK_COMMENT_REPLY("task_item_comment_replied"),
	DM_MENTION("chat_dm_mention"),
	DM_REPLY("chat_dm_message_replied"),
	GROUP_MENTION("chat_channel_mention"),
	GROUP_REPLY("chat_channel_message_replied"),
	NETWORK_INVITE("invited_to_network")
}

fun String?.toNotificationType() =
	when (this) {
		null -> NotificationType.NONE
		else -> NotificationType.values().firstOrNull { type -> type.serializedName == this }
			?: NotificationType.NONE
	}
