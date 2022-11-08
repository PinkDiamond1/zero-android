package com.zero.android.data.repository

import com.zero.android.data.conversion.toEntity
import com.zero.android.data.conversion.toModel
import com.zero.android.database.dao.UserDao
import com.zero.android.database.model.toModel
import com.zero.android.datastore.AppPreferences
import com.zero.android.network.service.AccountService
import com.zero.android.network.service.UserService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class UserRepositoryImpl
@Inject
constructor(
	private val preferences: AppPreferences,
	private val userDao: UserDao,
	private val userService: UserService,
	private val accountService: AccountService
) : UserRepository {

	override suspend fun getUser() = flow {
		userDao.getAll().firstOrNull()?.firstOrNull()?.let { cachedUser -> emit(cachedUser.toModel()) }

		userService.getUser().let { user ->
			userDao.insert(user.toEntity())
			emit(user.toModel())
		}
	}

	override suspend fun syncAccount(inviteCode: String) {
		preferences.token()?.let { accountService.syncAccount(it, inviteCode) }
	}
}
