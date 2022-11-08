package com.zero.android.common.system

interface NotificationManager {
	fun createMessageNotification(
		channelId: String,
		isGroupChannel: Boolean,
		title: String,
		text: String,
		image: String? = null
	)

	fun removeMessageNotifications(channelId: String)
}
