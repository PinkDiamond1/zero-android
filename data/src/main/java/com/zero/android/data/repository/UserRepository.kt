package com.zero.android.data.repository

import com.zero.android.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

	suspend fun getUser(): Flow<User>

	suspend fun syncAccount(inviteCode: String)
}
