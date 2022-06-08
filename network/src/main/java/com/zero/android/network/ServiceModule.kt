package com.zero.android.network

import com.zero.android.network.service.AccountService
import com.zero.android.network.service.NetworkService
import com.zero.android.network.service.ProfileService
import com.zero.android.network.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

	@Singleton
	@Provides
	fun provideUserService(retrofit: Retrofit) = retrofit.create(UserService::class.java)

	@Singleton
	@Provides
	fun provideAccountService(retrofit: Retrofit) = retrofit.create(AccountService::class.java)

	@Singleton
	@Provides
	fun provideProfileService(retrofit: Retrofit) = retrofit.create(ProfileService::class.java)

	@Singleton
	@Provides
	fun provideNetworkService(retrofit: Retrofit) = retrofit.create(NetworkService::class.java)
}