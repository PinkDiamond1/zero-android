package com.zero.android.network.chat.sendbird

import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.SendBirdPushHandler
import com.sendbird.android.SendBirdPushHelper
import com.zero.android.common.system.Logger
import com.zero.android.common.system.NotificationManager
import com.zero.android.network.chat.conversion.getNetworkId
import com.zero.android.network.extensions.parsed
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class SendBirdFCMService
@Inject
constructor(private val logger: Logger, private val notificationManager: NotificationManager) :
	SendBirdPushHandler() {

	override fun onNewToken(newToken: String?) {
		super.onNewToken(newToken)
		pushToken.set(newToken)
	}

	@Suppress("UNNECESSARY_SAFE_CALL")
	override fun onMessageReceived(context: Context?, remoteMessage: RemoteMessage?) {
		logger.d("onMessageReceived")

		try {
			if (remoteMessage?.data?.containsKey("sendbird") == true) {
				val sendbird = remoteMessage.data["sendbird"]?.let { JSONObject(it) } ?: return
				val channel = sendbird.get("channel") as JSONObject?
				val sender = sendbird.getJSONObject("sender")
				val networkId = channel?.getString("custom_type")?.let { getNetworkId(it) }
				val isDirectChannel = networkId == null
				var image =
					sendbird
						.getJSONObject("sender")
						?.getString("profile_url")
						?.let { JSONObject(it) }
						?.getString("profileImage")

				val title: String?
				val message: String?
				if (isDirectChannel) {
					title = sender?.getString("name")
					message = sendbird.getString("message")
				} else {
					title = channel?.getString("name")
					message =
						(sender?.getString("name")?.let { "$it: " } ?: "") + sendbird.getString("message")
					image = channel?.getString("coverUrl") ?: image
				}

				channel?.getString("channel_url")?.let { url ->
					notificationManager.createMessageNotification(
						channelId = url,
						title = title ?: "",
						text = message ?: "",
						image = image
					)
				}
				SendBird.markAsDelivered(remoteMessage.data)
			}
		} catch (e: JSONException) {
			e.printStackTrace()
		}
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
					coroutine.resumeWithException(e.parsed)
				}
			}
		}
	}
}
