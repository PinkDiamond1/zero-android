package com.zero.android.models.enums

enum class NotificationCategory(val serializedName: String) {
	NONE(""),
	FEED("feed"),
	CHAT("chat"),
	TASK("task"),
	INVITE("invite")
}

fun NotificationType?.toNotificationCategory(): NotificationCategory {
	this ?: return NotificationCategory.NONE
	return if (serializedName.contains("task")) NotificationCategory.FEED
	else if (serializedName.contains("feed") || serializedName.contains("comment_added")) {
		NotificationCategory.FEED
	} else if (serializedName.contains("invite")) NotificationCategory.INVITE
	else if (this == NotificationType.NONE) NotificationCategory.NONE else NotificationCategory.CHAT
}
