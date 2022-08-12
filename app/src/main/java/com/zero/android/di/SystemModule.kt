package com.zero.android.di

import com.zero.android.common.system.PermissionsManager
import com.zero.android.common.system.PushNotifications
import com.zero.android.system.PermissionsManagerImpl
import com.zero.android.system.PushNotificationsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SystemModule {

	@Binds fun bindPermissionsManager(manager: PermissionsManagerImpl): PermissionsManager

	@Binds fun bindPushNotifications(manager: PushNotificationsImpl): PushNotifications
}
