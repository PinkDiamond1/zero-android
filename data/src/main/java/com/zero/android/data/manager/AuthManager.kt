package com.zero.android.data.manager

import android.content.Context
import com.zero.android.models.AuthCredentials

interface AuthManager {
	suspend fun login(credentials: AuthCredentials)
	suspend fun logout(context: Context)

	suspend fun onLogin(credentials: AuthCredentials)
}
