package com.zero.android.data.manager

import android.content.Context
import com.zero.android.common.system.Logger
import com.zero.android.common.system.PushNotifications
import com.zero.android.data.conversion.toModel
import com.zero.android.data.repository.AuthRepository
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.data.repository.UserRepository
import com.zero.android.datastore.AppPreferences
import com.zero.android.models.AuthCredentials
import com.zero.android.network.service.ChannelService
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.take
import javax.inject.Inject

internal class AuthManagerImpl
@Inject
constructor(
	private val authRepository: AuthRepository,
	private val userRepository: UserRepository,
	private val channelService: ChannelService,
	private val channelRepository: ChannelRepository,
	private val networkRepository: NetworkRepository,
	private val preferences: AppPreferences,
	private val connectionManager: ConnectionManager,
	private val pushNotifications: PushNotifications,
	private val dataCleaner: DataCleaner,
	private val logger: Logger
) : AuthManager {

	override suspend fun login(credentials: AuthCredentials) {
		preferences.setAuthCredentials(credentials)
		try {
			val user = userRepository.getUser().last()

			preferences.setUserId(user.id)
			preferences.setUserImage(user.profile.profileImage)
			authRepository.refreshChatAccessToken(credentials.accessToken)

			onLogin(credentials)

			// Joining Public Channels
			networkRepository.getNetworks().take(2).lastOrNull()?.let { networks ->
				networks.forEach { network ->
					val channels = channelService.getPublicChannels(network.id)
					for (channel in channels) {
						if (channel.members.find { it.id == user.id } == null) {
							channelRepository.joinChannel(channel.toModel())
						}
					}
				}
			}
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
	}
}
