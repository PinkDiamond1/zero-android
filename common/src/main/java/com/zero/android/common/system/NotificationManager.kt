package com.zero.android.common.system

import android.app.Notification

interface NotificationManager {
	fun createMessageNotification(
		channelId: String,
		title: String,
		text: String,
		image: String? = null
	)

	fun removeMessageNotifications(channelId: String)

	fun createSyncNotification(notificationId: Int): Notification
}
