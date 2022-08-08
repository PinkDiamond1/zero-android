package com.zero.android.data.repository

import android.content.Context
import com.zero.android.common.system.Logger
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.conversion.toModel
import com.zero.android.data.manager.ConnectionManager
import com.zero.android.data.manager.DataCleaner
import com.zero.android.database.dao.UserDao
import com.zero.android.database.model.toModel
import com.zero.android.datastore.AppPreferences
import com.zero.android.datastore.ChatPreferences
import com.zero.android.models.AuthCredentials
import com.zero.android.network.service.AccessService
import com.zero.android.network.service.UserService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class UserRepositoryImpl
@Inject
constructor(
	private val userDao: UserDao,
	private val preferences: AppPreferences,
	private val chatPreferences: ChatPreferences,
	private val userService: UserService,
	private val accessService: AccessService,
	private val connectionManager: ConnectionManager,
	private val dataCleaner: DataCleaner,
	private val logger: Logger
) : UserRepository {

	override suspend fun login(credentials: AuthCredentials) {
		preferences.setAuthCredentials(credentials)
		try {
			val user = getUser().last()

			preferences.setUserId(user.id)
			refreshChatAccessToken(credentials.accessToken)

			connectionManager.connect()
		} catch (e: Exception) {
			logger.e(e)
			dataCleaner.clean()
			throw e
		}
	}

	override suspend fun logout(context: Context) {
		dataCleaner.clean()
		connectionManager.disconnect(context)
	}

	private suspend fun refreshChatAccessToken(accessToken: String) {
		accessService.getChatAccessToken(accessToken).let {
			chatPreferences.setChatToken(it.chatAccessToken)
		}
	}

	override suspend fun getUser() = flow {
		userDao.getAll().firstOrNull()?.firstOrNull()?.let { cachedUser -> emit(cachedUser.toModel()) }

		userService.getUser().let { user ->
			userDao.insert(user.toEntity())
			emit(user.toModel())
		}
	}
}
