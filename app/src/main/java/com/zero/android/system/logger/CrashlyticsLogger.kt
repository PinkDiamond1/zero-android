package com.zero.android.system.logger

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.zero.android.common.system.Logger
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CrashlyticsLogger @Inject constructor() : Logger {

	override fun setup(release: Boolean) {
		Timber.plant(CrashReportingTree())
	}

	override fun d(message: String) = Timber.d(message)

	override fun d(t: Throwable, message: String?) = Timber.d(t, message)

	override fun i(message: String) = Timber.i(message)

	override fun i(t: Throwable, message: String?) = Timber.i(t, message)

	override fun w(message: String) = Timber.w(message)

	override fun w(t: Throwable, message: String?) = Timber.w(t, message)

	override fun e(message: String, t: Throwable?) = Timber.e(message, t)

	override fun e(t: Throwable) = Timber.e(t)

	private class CrashReportingTree : Timber.Tree() {
		override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
			if (priority == Log.VERBOSE || priority == Log.DEBUG) return
			FirebaseCrashlytics.getInstance().recordException(t ?: Exception(message))
		}
	}
}
