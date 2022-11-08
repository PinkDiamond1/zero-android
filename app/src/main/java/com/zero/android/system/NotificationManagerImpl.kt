package com.zero.android.system

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.ArrayMap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import com.zero.android.MainActivity
import com.zero.android.R
import com.zero.android.common.R.string
import com.zero.android.common.extensions.runOnMainThread
import com.zero.android.common.extensions.withScope
import com.zero.android.common.system.NotificationManager
import com.zero.android.data.manager.ImageLoader
import com.zero.android.feature.messages.navigation.MessagesDestination
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationManagerImpl
@Inject
constructor(
	@ApplicationContext private val context: Context,
	private val imageLoader: ImageLoader
) : NotificationManager {

	private companion object {
		const val MESSAGES_CHANNEL_ID = "Messages"
	}

	private data class Notification(
		val id: Int,
		val tag: String? = null,
		val title: String,
		val message: String,
		val image: Bitmap?
	)

	private val notificationsMap: ArrayMap<String, MutableList<Notification>> by lazy { ArrayMap() }

	private fun createNotificationChannel(
		id: String,
		name: String,
		description: String,
		importance: Int = android.app.NotificationManager.IMPORTANCE_DEFAULT
	) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel =
				NotificationChannel(id, name, importance).apply { this.description = description }
			(context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager)
				.let { it.createNotificationChannel(channel) }
		}
	}

	override fun createMessageNotification(
		channelId: String,
		isGroupChannel: Boolean,
		title: String,
		text: String,
		image: String?
	) {
		createNotificationChannel(
			id = MESSAGES_CHANNEL_ID,
			name = context.getString(string.notification_channel_messages),
			description = context.getString(string.notification_channel_messages_description)
		)

		val intent =
			Intent(context, MainActivity::class.java).apply {
				flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
				data = Uri.parse(MessagesDestination.deeplink(channelId, isGroupChannel))
			}
		val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

		withScope(Dispatchers.IO) {
			val mImage = image?.let { imageLoader.load(it).firstOrNull() }

			runOnMainThread {
				val id = System.currentTimeMillis().toInt()

				NotificationCompat.Builder(context, MESSAGES_CHANNEL_ID)
					.createNotificationsByTag(
						tag = channelId,
						notification =
						Notification(
							id = id,
							tag = channelId,
							title = title,
							message = text,
							image = mImage
						),
						pendingIntent = pendingIntent
					)
			}
		}
	}

	override fun removeMessageNotifications(channelId: String) = removeNotifications(tag = channelId)

	private fun NotificationCompat.Builder.createNotificationsByTag(
		tag: String,
		notification: Notification,
		pendingIntent: PendingIntent
	) {
		setSmallIcon(R.drawable.ic_launcher_foreground)
		setLargeIcon(notification.image)
		setContentTitle(notification.title)
		setContentText(notification.message)
		setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
		priority = NotificationCompat.PRIORITY_DEFAULT
		setContentIntent(pendingIntent)
		setAutoCancel(true)

		with(NotificationManagerCompat.from(context)) {
			notify(tag, notification.id, build())

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
				notificationsMap[tag] =
					notificationsMap.getOrDefault(tag, mutableListOf()).apply { add(notification) }
			}
		}
	}

	private fun removeNotifications(tag: String? = null, id: Int? = null) {
		if (id != null) NotificationManagerCompat.from(context).cancel(id)
		else if (tag != null) {
			getActiveNotifications(tag).forEach {
				NotificationManagerCompat.from(context).cancel(tag, it.id)
			}
		}
	}

	private fun getActiveNotifications(tag: String): List<Notification> {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			(context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager?)
				?.run {
					activeNotifications.map {
						Notification(
							id = it.id,
							tag = it.tag,
							title = it.notification.extras.getString("android.title", ""),
							message = it.notification.extras.getString("android.text", ""),
							image = it.notification.getLargeIcon()?.loadDrawable(context)?.toBitmap()
						)
					}
				}
		} else {
			notificationsMap[tag]
		}
			?: emptyList()
	}
}
