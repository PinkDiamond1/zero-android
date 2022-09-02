package com.zero.android.network.auth

import android.content.Context
import com.zero.android.models.AuthCredentials
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AuthService {

	suspend fun login(email: String, password: String): Flow<AuthCredentials>

	suspend fun loginWithApple(context: Context): Flow<AuthCredentials>

	suspend fun loginWithGoogle(context: Context): Flow<AuthCredentials>

	suspend fun forgotPassword(email: String): Flow<Unit>

	suspend fun register(
		name: String,
		email: String,
		password: String,
		profilePic: File?
	): Flow<AuthCredentials>

	suspend fun revokeToken(token: String): Flow<Unit>
}
