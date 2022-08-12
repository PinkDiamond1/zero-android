package com.zero.android.system

import android.content.Context
import com.onesignal.OneSignal
import com.zero.android.BuildConfig
import com.zero.android.common.system.PushNotifications
import com.zero.android.data.delegates.Preferences
import com.zero.android.network.chat.ChatProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class PushNotificationsImpl
@Inject
constructor(
	@ApplicationContext private val context: Context,
	private val preferences: Preferences,
	private val chatProvider: ChatProvider
) : PushNotifications {

	override fun initialize() {
		OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

		OneSignal.initWithContext(context)
		OneSignal.setAppId(BuildConfig.ONESIGNAL_ID)

		chatProvider.registerNotificationHandler()
	}

	override suspend fun subscribe() {
		OneSignal.sendTags(JSONObject(mapOf("user_id" to preferences.userId())))
		chatProvider.registerDevice()
	}
}
