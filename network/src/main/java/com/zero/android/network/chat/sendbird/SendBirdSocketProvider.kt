package com.zero.android.network.chat.sendbird

import com.sendbird.android.SendBird
import com.zero.android.common.system.Logger
import com.zero.android.network.SocketListener
import com.zero.android.network.SocketProvider
import java.util.*
import javax.inject.Inject

internal class SendBirdSocketProvider @Inject constructor(private val logger: Logger) :
	SocketProvider {

	private val socketHandlerId by lazy { UUID.randomUUID() }

	override fun startListening(listener: SocketListener) {
		SendBird.addChannelHandler(socketHandlerId.toString(), SendBirdSocketHandler(listener))
		logger.i("Socket started")
	}

	override fun stopListening() {
		SendBird.removeChannelHandler(socketHandlerId.toString())
		logger.i("Socket closed")
	}
}
