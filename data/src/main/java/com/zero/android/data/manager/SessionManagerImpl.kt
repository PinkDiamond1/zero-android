package com.zero.android.data.manager

import android.content.Context
import com.zero.android.common.system.Logger
import com.zero.android.common.system.PushNotifications
import com.zero.android.data.repository.AuthRepository
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.data.repository.UserRepository
import com.zero.android.datastore.AppPreferences
import com.zero.android.models.AuthCredentials
import com.zero.android.models.User
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.take
import javax.inject.Inject

internal class SessionManagerImpl
@Inject
constructor(
	private val authRepository: AuthRepository,
	private val userRepository: UserRepository,
	private val channelRepository: ChannelRepository,
	private val networkRepository: NetworkRepository,
	private val preferences: AppPreferences,
	private val connectionManager: ConnectionManager,
	private val workManager: WorkManager,
	private val pushNotifications: PushNotifications,
	private val dataCleaner: DataCleaner,
	private val logger: Logger
) : SessionManager {

	override suspend fun login(credentials: AuthCredentials) {
		preferences.setAuthCredentials(credentials)
		try {
			val user = userRepository.getUser().last()

			preferences.setUserId(user.id)
			preferences.setUserImage(user.profile.profileImage)
			authRepository.refreshChatAccessToken(credentials.accessToken)

			onLogin(credentials)
			setupAccount(user)

			preferences.setSetupComplete()
		} catch (e: Exception) {
			logger.e(e)
			dataCleaner.clean()
			throw e
		}
	}

	override suspend fun onLogin(credentials: AuthCredentials) {
		connectionManager.connect()
		pushNotifications.subscribe()
	}

	override suspend fun logout(context: Context) {
		dataCleaner.clean()
		connectionManager.disconnect(context)
		workManager.cancelAll()
	}

	private suspend fun setupAccount(user: User) {
		// Joining Public Channels
		networkRepository.getNetworks().take(2).lastOrNull()?.let { networks ->
			channelRepository.joinPublicChannels(networks.firstOrNull()?.id ?: return)
			networks.subList(1, networks.size).forEach { workManager.joinPublicChannels(it) }
		}
	}
}
