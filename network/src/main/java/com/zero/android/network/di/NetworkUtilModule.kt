package com.zero.android.network.di

import android.content.Context
import com.zero.android.network.util.NetworkFileUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkUtilModule {

	@Singleton
	@Provides
	fun provideNetworkMediaUtil(@ApplicationContext context: Context) = NetworkFileUtil(context)
}
