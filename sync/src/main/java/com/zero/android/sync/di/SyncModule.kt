package com.zero.android.sync.di

import com.zero.android.data.manager.WorkManager
import com.zero.android.sync.WorkManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SyncModule {

	@Binds fun bindWorkManager(workManager: WorkManagerImpl): WorkManager
}
