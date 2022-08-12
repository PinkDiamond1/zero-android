package com.zero.android.common.system

interface PushNotifications {

	fun initialize()

	suspend fun subscribe()
}
