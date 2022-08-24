package com.zero.android.di

import com.zero.android.common.system.Logger
import com.zero.android.system.logger.ConsoleLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

	@Singleton @Provides
	fun provideLogger(): Logger = ConsoleLogger()
}
