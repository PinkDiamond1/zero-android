package com.zero.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoaderFactory
import com.zero.android.common.system.Logger
import com.zero.android.common.system.PushNotifications
import com.zero.android.data.manager.ImageLoader
import com.zero.android.network.NetworkInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ZeroApp : Application(), ImageLoaderFactory, Configuration.Provider {

	@Inject lateinit var logger: Logger

	@Inject lateinit var networkInitializer: NetworkInitializer

	@Inject lateinit var pushNotifications: PushNotifications

	@Inject lateinit var workerFactory: HiltWorkerFactory

	override fun onCreate() {
		super.onCreate()

		logger.setup(BuildConfig.DEBUG)
		pushNotifications.initialize()
		networkInitializer.initialize()
	}

	override fun newImageLoader() = ImageLoader.getImageLoader(this)

	override fun getWorkManagerConfiguration() =
		Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.setMinimumLoggingLevel(
				if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR
			)
			.build()
}
