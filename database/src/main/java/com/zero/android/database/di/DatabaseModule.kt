package com.zero.android.database.di

import android.content.Context
import com.zero.android.database.AppDatabase
import com.zero.android.database.DatabaseCleaner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

	@Provides
	@Singleton
	fun provideDatabaseCleaner(appDatabase: AppDatabase) = DatabaseCleaner(appDatabase)
}
