package com.zero.android.database

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

	@Provides fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

	@Provides fun provideMemberDao(database: AppDatabase): MemberDao = database.memberDao()

	@Provides fun provideProfileDao(database: AppDatabase): ProfileDao = database.profileDao()

	@Provides fun provideNetworkDao(database: AppDatabase): NetworkDao = database.networkDao()

	@Provides
	fun provideGroupChannelDao(database: AppDatabase): GroupChannelDaoImpl =
		database.groupChannelDao()

	@Provides
	fun provideDirectChannelDao(database: AppDatabase): DirectChannelDaoImpl =
		database.directChannelDao()

	@Provides
	fun provideChannelDao(database: AppDatabase, messageDao: MessageDao) =
		ChannelDao(
			database.directChannelDao(),
			database.groupChannelDao(),
			database.memberDao(),
			messageDao
		)

	@Provides
	fun provideMessageDao(database: AppDatabase) =
		MessageDao(database.messageDao(), database.memberDao())
}
