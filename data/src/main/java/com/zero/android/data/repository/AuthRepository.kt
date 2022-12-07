package com.zero.android.data.repository

import android.content.Context
import com.zero.android.models.AuthCredentials
import java.io.File

interface AuthRepository {

	suspend fun refreshChatAccessToken(accessToken: String)

	suspend fun login(email: String, password: String): AuthCredentials

	suspend fun loginWithApple(context: Context): AuthCredentials

	suspend fun loginWithGoogle(context: Context): AuthCredentials

	suspend fun forgotPassword(email: String)

	suspend fun register(
		name: String,
		email: String,
		password: String,
		inviteCode: String?,
		profilePic: File?
	): AuthCredentials

	suspend fun revokeToken()
}
