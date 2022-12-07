package com.zero.android.data.repository

import android.content.Context
import com.zero.android.data.delegates.Preferences
import com.zero.android.datastore.ChatPreferences
import com.zero.android.models.AuthCredentials
import com.zero.android.network.model.request.CreateUserRequest
import com.zero.android.network.service.AccessService
import com.zero.android.network.service.AuthService
import com.zero.android.network.service.ZeroAuthService
import java.io.File
import javax.inject.Inject

internal class AuthRepositoryImpl
@Inject
constructor(
	private val preferences: Preferences,
	private val chatPreferences: ChatPreferences,
	private val authService: AuthService,
	private val accessService: AccessService,
	private val zeroAuthService: ZeroAuthService
) : AuthRepository {

	override suspend fun refreshChatAccessToken(accessToken: String) {
		accessService.getChatAccessToken(accessToken).let {
			chatPreferences.setChatToken(it.chatAccessToken)
		}
	}

	override suspend fun login(email: String, password: String) = authService.login(email, password)

	override suspend fun loginWithGoogle(context: Context) = authService.loginWithGoogle(context)

	override suspend fun loginWithApple(context: Context) = authService.loginWithApple(context)

	override suspend fun forgotPassword(email: String) = authService.forgotPassword(email)

	override suspend fun register(
		name: String,
		email: String,
		password: String,
		inviteCode: String?,
		profilePic: File?
	): AuthCredentials {
		authService.register(name, email, password, inviteCode ?: "", profilePic)
		val credentials = login(email, password)
		createUser(credentials, name, inviteCode)
		return credentials
	}

	override suspend fun revokeToken() {
		preferences.credentials()?.refreshToken?.let { authService.revokeToken(it) }
	}

	private suspend fun createUser(credentials: AuthCredentials, name: String, inviteCode: String?) {
		val token = "Bearer ${credentials.accessToken}"
		val payload = CreateUserRequest(inviteCode ?: "", CreateUserRequest.UserInfo(name, name, ""))
		zeroAuthService.createUser(token, payload)
	}
}
