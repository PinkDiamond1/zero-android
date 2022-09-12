package com.zero.android.network.chat.sendbird

import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.android.SendBirdException
import com.sendbird.android.SendBirdPushHandler
import com.sendbird.android.SendBirdPushHelper
import com.zero.android.common.system.Logger
import com.zero.android.common.system.NotificationManager
import com.zero.android.network.chat.conversion.getNetworkId
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SendBirdFCMService
@Inject
constructor(private val logger: Logger, private val notificationManager: NotificationManager) :
	SendBirdPushHandler() {

	override fun onNewToken(newToken: String?) {
		super.onNewToken(newToken)
		pushToken.set(newToken)
	}

	override fun onMessageReceived(context: Context?, remoteMessage: RemoteMessage?) {
		logger.d("onMessageReceived")

		try {
			if (remoteMessage?.data?.containsKey("sendbird") == true) {
				val sendbird = remoteMessage.data["sendbird"]?.let { JSONObject(it) }
				val channel = sendbird?.get("channel") as JSONObject?
				val networkId = channel?.getString("custom_type")?.let { getNetworkId(it) }

				channel?.getString("channel_url")?.let { url ->
					sendNotification(
						id = url,
						isGroupChannel = networkId != null,
						title = remoteMessage.data["push_title"],
						body = remoteMessage.data["message"]
					)
				}
			}
		} catch (e: JSONException) {
			e.printStackTrace()
		}
	}

	private fun sendNotification(id: String, isGroupChannel: Boolean, title: String?, body: String?) {
		notificationManager.createMessageNotification(
			id = id,
			isGroupChannel = isGroupChannel,
			title = title ?: "",
			text = body ?: ""
		)
	}

	override fun isUniquePushToken() = false

	override fun alwaysReceiveMessage() = false

	companion object {

		private val pushToken: AtomicReference<String> = AtomicReference()

		suspend fun getPushToken() = suspendCancellableCoroutine { coroutine ->
			val token = pushToken.get()
			if (!token.isNullOrEmpty()) {
				return@suspendCancellableCoroutine coroutine.resume(token)
			}
			SendBirdPushHelper.getPushToken { newToken: String?, e: SendBirdException? ->
				if (e == null) {
					pushToken.set(newToken)
					newToken?.let { coroutine.resume(it) }
				} else {
					coroutine.resumeWithException(e)
				}
			}
		}
	}
}
