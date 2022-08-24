package com.zero.android.data.repository

import com.zero.android.datastore.ChatPreferences
import com.zero.android.network.service.AccessService
import javax.inject.Inject

class AuthRepositoryImpl
@Inject
constructor(
	private val chatPreferences: ChatPreferences,
	private val accessService: AccessService
) : AuthRepository {

	override suspend fun refreshChatAccessToken(accessToken: String) {
		accessService.getChatAccessToken(accessToken).let {
			chatPreferences.setChatToken(it.chatAccessToken)
		}
	}
}
