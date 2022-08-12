package com.zero.android.data.repository

interface AuthRepository {

	suspend fun refreshChatAccessToken(accessToken: String)
}
