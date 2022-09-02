package com.zero.android.data.di

import com.zero.android.data.repository.*
import com.zero.android.data.repository.AuthRepository
import com.zero.android.data.repository.AuthRepositoryImpl
import com.zero.android.data.repository.mediaplayer.MediaPlayerRepository
import com.zero.android.data.repository.mediaplayer.MediaPlayerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

	@Binds fun bindAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

	@Binds fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository

	@Binds fun bindNetworkRepository(networkRepository: NetworkRepositoryImpl): NetworkRepository

	@Binds fun bindChannelRepository(channelRepository: ChannelRepositoryImpl): ChannelRepository

	@Binds fun bindChatRepository(chatRepository: ChatRepositoryImpl): ChatRepository

	@Binds
	fun bindMediaPlayerRepository(
		mediaPlayerRepository: MediaPlayerRepositoryImpl
	): MediaPlayerRepository
}
