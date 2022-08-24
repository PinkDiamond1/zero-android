package com.zero.android.data.di

import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.common.usecases.SearchTriggerUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

	@Singleton
	@Binds
	fun bindSearchTriggerUseCase(
		searchTriggerUseCaseImpl: SearchTriggerUseCaseImpl
	): SearchTriggerUseCase
}
