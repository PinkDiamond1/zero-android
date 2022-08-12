package com.zero.android.network

import com.zero.android.network.chat.ChatProvider
import javax.inject.Inject

class NetworkInitializer @Inject constructor(private val chatProvider: ChatProvider) {

	fun initialize() {
		chatProvider.initialize()
	}
}
