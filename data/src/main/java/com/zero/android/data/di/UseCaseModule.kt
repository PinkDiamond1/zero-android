package com.zero.android.data.di

import com.zero.android.common.usecases.SearchTriggerUseCase
import com.zero.android.common.usecases.SearchTriggerUseCaseImpl
import com.zero.android.common.usecases.ThemePaletteUseCase
import com.zero.android.common.usecases.ThemePaletteUseCaseImpl
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

	@Singleton
	@Binds
	fun bindThemePaletteUseCase(themePaletteUseCaseImpl: ThemePaletteUseCaseImpl): ThemePaletteUseCase
}
