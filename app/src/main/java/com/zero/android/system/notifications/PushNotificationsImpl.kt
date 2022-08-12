package com.zero.android.system.notifications

import android.content.Context
import com.onesignal.OneSignal
import com.zero.android.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PushNotificationsImpl @Inject constructor(@ApplicationContext private val context: Context) :
	PushNotifications {

	override fun initialize() {
		OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

		OneSignal.initWithContext(context)
		OneSignal.setAppId(BuildConfig.ONESIGNAL_ID)
	}
}
