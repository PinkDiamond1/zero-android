package com.zero.android.data.repository

import android.content.Context
import com.zero.android.models.AuthCredentials
import com.zero.android.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

	suspend fun login(credentials: AuthCredentials)

	suspend fun logout(context: Context)

	suspend fun getUser(): Flow<User>
}
