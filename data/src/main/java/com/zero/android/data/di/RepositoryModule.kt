package com.zero.android.data.di

import com.zero.android.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

	@Binds fun bindAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

	@Binds fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository

	@Binds fun bindMemberRepository(userRepository: MemberRepositoryImpl): MemberRepository

	@Binds fun bindNetworkRepository(networkRepository: NetworkRepositoryImpl): NetworkRepository

	@Binds fun bindChannelRepository(channelRepository: ChannelRepositoryImpl): ChannelRepository

	@Binds fun bindChatRepository(chatRepository: ChatRepositoryImpl): ChatRepository

	@Binds
	fun bindChatMediaRepository(chatMediaRepository: ChatMediaRepositoryImpl): ChatMediaRepository

	@Binds fun bindInviteRepository(inviteRepository: InviteRepositoryImpl): InviteRepository

	@Binds fun bindFileRepository(fileRepository: FileRepositoryImpl): FileRepository
}
