package com.zero.android

import android.app.Application
import com.zero.android.common.system.Logger
import com.zero.android.common.system.PushNotifications
import com.zero.android.network.NetworkInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ZeroApp : Application() {

	@Inject lateinit var logger: Logger

	@Inject lateinit var networkInitializer: NetworkInitializer

	@Inject lateinit var pushNotifications: PushNotifications

	override fun onCreate() {
		super.onCreate()

		logger.setup(BuildConfig.DEBUG)
		pushNotifications.initialize()
		networkInitializer.initialize()
	}
}
