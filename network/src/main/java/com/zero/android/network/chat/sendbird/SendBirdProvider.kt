package com.zero.android.network.chat.sendbird

import android.content.Context
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.SendBirdPushHelper
import com.sendbird.android.handlers.InitResultHandler
import com.zero.android.common.extensions.withScope
import com.zero.android.common.system.Logger
import com.zero.android.network.BuildConfig
import com.zero.android.network.chat.ChatProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class SendBirdProvider
@Inject
constructor(
	@ApplicationContext private val context: Context,
	private val logger: Logger,
	private val fcmService: SendBirdFCMService = SendBirdFCMService(logger)
) : ChatProvider {

	override fun initialize() {
		SendBird.init(
			BuildConfig.SEND_BIRD_ID,
			context,
			true,
			object : InitResultHandler {
				override fun onMigrationStarted() {
					logger.i("There's an update in Sendbird server.")
				}

				override fun onInitFailed(e: SendBirdException) {
					logger.e(
						"SendBird initialize failed. SDK will still operate properly as if useLocalCaching is set to false.",
						e
					)
				}

				override fun onInitSucceed() {
					logger.i("SendBird Connected")
				}
			}
		)
	}

	override suspend fun connect(userId: String, accessToken: String?) = suspendCoroutine {
		logger.i("Connecting to SendBird")
		SendBird.connect(userId, accessToken) { user, e ->
			withScope(Dispatchers.IO) { registerDevice() }
			if (user != null) {
				if (e != null) {
					// Proceed in offline mode with the data stored in the local database.
					// Later, connection will be made automatically
					// and can be notified through the ConnectionHandler.onReconnectSucceeded().
					it.resume(Unit)
				} else {
					// Proceed in online mode.
					it.resume(Unit)
				}
			} else {
				// Handle error.
				logger.w("Failed to connect to SendBird")
				it.resumeWithException(e)
			}
		}
	}

	override suspend fun disconnect(context: Context) = suspendCoroutine { coroutine ->
		logger.d("Disconnecting from SendBird")
		withScope(Dispatchers.IO) {
			unregisterNotificationHandler()
			SendBird.unregisterPushTokenAllForCurrentUser {
				SendBird.clearCachedData(context) {}
				SendBird.disconnect { coroutine.resume(Unit) }
			}
		}
	}

	override suspend fun registerDevice() = suspendCoroutine {
		withScope(Dispatchers.IO) {
			val deviceToken = SendBirdFCMService.getPushToken()

			logger.i("SendBird Push Token: $deviceToken")
			SendBird.registerPushTokenForCurrentUser(deviceToken) { status, e ->
				logger.d("SendBird Push Token: $status")
				if (e != null) {
					logger.e(e)
					it.resumeWithException(e)
				} else {
					it.resume(Unit)
				}
			}
		}
	}

	override fun registerNotificationHandler() {
		logger.d("Registering to SendBird Notifications")
		SendBirdPushHelper.registerPushHandler(fcmService)
	}

	private suspend fun unregisterNotificationHandler() = suspendCoroutine {
		logger.d("Unregistering from SendBird Notifications")
		SendBirdPushHelper.unregisterPushHandler(
			true,
			object : SendBirdPushHelper.OnPushRequestCompleteListener {
				override fun onComplete(p0: Boolean, p1: String?) {
					it.resume(true)
				}

				override fun onError(e: SendBirdException?) {
					e?.let { error -> logger.e(error) }
					it.resume(false)
				}
			}
		)
	}
}
