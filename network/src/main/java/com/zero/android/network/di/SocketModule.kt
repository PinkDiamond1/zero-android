package com.zero.android.network.di

import android.content.Context
import com.zero.android.common.system.Logger
import com.zero.android.common.system.NotificationManager
import com.zero.android.network.SocketProvider
import com.zero.android.network.chat.ChatProvider
import com.zero.android.network.chat.sendbird.SendBirdFCMService
import com.zero.android.network.chat.sendbird.SendBirdProvider
import com.zero.android.network.chat.sendbird.SendBirdSocketProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

	@Singleton
	@Provides
	fun provideSocketProvider(logger: Logger): SocketProvider = SendBirdSocketProvider(logger)

	@Singleton
	@Provides
	fun provideChatProvider(
		@ApplicationContext context: Context,
		logger: Logger,
		fcmService: SendBirdFCMService
	): ChatProvider = SendBirdProvider(context, logger, fcmService)

	@Singleton
	@Provides
	fun provideSendBirdFCMService(
		logger: Logger,
		notificationManager: NotificationManager
	): SendBirdFCMService = SendBirdFCMService(logger, notificationManager)
}
