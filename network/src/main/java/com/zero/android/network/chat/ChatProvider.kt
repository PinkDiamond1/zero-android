package com.zero.android.network.chat

import android.content.Context

interface ChatProvider {

	fun initialize()

	suspend fun connect(userId: String, accessToken: String?)

	suspend fun disconnect(context: Context)

	suspend fun registerDevice()

	fun registerNotificationHandler()
}
