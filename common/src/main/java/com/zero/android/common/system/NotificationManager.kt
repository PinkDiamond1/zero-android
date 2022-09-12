package com.zero.android.common.system

interface NotificationManager {
	fun createMessageNotification(id: String, isGroupChannel: Boolean, title: String, text: String)
}
