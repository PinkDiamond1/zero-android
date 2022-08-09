package com.zero.android.data.manager

import android.content.Context
import com.zero.android.datastore.AppPreferences
import com.zero.android.datastore.ChatPreferences
import com.zero.android.network.SocketProvider
import com.zero.android.network.chat.ChatProvider
import javax.inject.Inject

class ConnectionManagerImpl
@Inject
constructor(
	private val chatProvider: ChatProvider,
	private val socketProvider: SocketProvider,
	private val preferences: AppPreferences,
	private val chatPreferences: ChatPreferences,
	private val socketListener: AppSocketListenerImpl
) : ConnectionManager {

	override suspend fun connect() {
		chatProvider.connect(preferences.userId(), chatPreferences.chatToken())
		socketProvider.startListening(socketListener)
	}

	override suspend fun disconnect(context: Context) {
		chatProvider.disconnect(context)
		clear()
	}

	override suspend fun clear() = socketProvider.stopListening()
}
