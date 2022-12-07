package com.zero.android.data.di

import com.zero.android.data.formatter.NotificationParser
import com.zero.android.data.formatter.NotificationParserImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface FormatterModule {

	@Singleton
	@Binds
	fun bindNotificationParser(notificationParser: NotificationParserImpl): NotificationParser
}
