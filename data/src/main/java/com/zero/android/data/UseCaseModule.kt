package com.zero.android.data

import com.zero.android.common.usecases.SearchTriggerManager
import com.zero.android.common.usecases.SearchTriggerUseCase
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
    fun bindSearchTriggerUseCase(searchTriggerManager: SearchTriggerManager): SearchTriggerUseCase
}
