package com.zero.android.network.chat.sendbird

import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.android.SendBirdPushHandler

internal class SendBirdFCMService : SendBirdPushHandler() {

	override fun onNewToken(newToken: String?) {
		super.onNewToken(newToken)
	}

	override fun onMessageReceived(p0: Context?, p1: RemoteMessage?) {
		TODO("Not yet implemented")
	}
}
