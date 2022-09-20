package com.zero.android.system

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zero.android.MainActivity
import com.zero.android.R
import com.zero.android.common.R.string
import com.zero.android.common.system.NotificationManager
import com.zero.android.feature.messages.navigation.MessagesDestination
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationManagerImpl
@Inject
constructor(@ApplicationContext private val context: Context) : NotificationManager {

	private companion object {
		const val MESSAGES_CHANNEL_ID = "Messages"
	}

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
		id: String,
		isGroupChannel: Boolean,
		title: String,
		text: String
	) {
		createNotificationChannel(
			id = MESSAGES_CHANNEL_ID,
			name = context.getString(string.notification_channel_messages),
			description = context.getString(string.notification_channel_messages_description)
		)

		val intent =
			Intent(context, MainActivity::class.java).apply {
				flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
				data = Uri.parse(MessagesDestination.deeplink(id, isGroupChannel))
			}

		val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

		val builder =
			NotificationCompat.Builder(context, MESSAGES_CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_launcher_foreground)
				.setContentTitle(title)
				.setContentText(text)
				.setStyle(NotificationCompat.BigTextStyle().bigText(text))
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true)

		with(NotificationManagerCompat.from(context)) {
			notify(System.currentTimeMillis().toInt(), builder.build())
		}
	}
}
