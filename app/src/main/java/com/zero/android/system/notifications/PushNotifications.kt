package com.zero.android.system.notifications

interface PushNotifications {

	fun initialize()

	suspend fun subscribe(deviceToken: String)
}
