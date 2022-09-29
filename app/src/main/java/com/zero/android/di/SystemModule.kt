package com.zero.android.di

import com.zero.android.common.system.NotificationManager
import com.zero.android.common.system.PermissionsManager
import com.zero.android.common.system.PushNotifications
import com.zero.android.common.system.SoundManager
import com.zero.android.system.NotificationManagerImpl
import com.zero.android.system.PermissionsManagerImpl
import com.zero.android.system.PushNotificationsImpl
import com.zero.android.system.SoundManagerImpl
import com.zero.android.system.ThemeManagerImpl
import com.zero.android.ui.maanger.ThemeManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SystemModule {

	@Binds fun bindPermissionsManager(manager: PermissionsManagerImpl): PermissionsManager

	@Binds fun bindPushNotifications(manager: PushNotificationsImpl): PushNotifications

	@Binds fun bindNotificationManager(manager: NotificationManagerImpl): NotificationManager

	@Binds fun bindSoundManager(manager: SoundManagerImpl): SoundManager

	@Binds fun bindThemeManager(themeManager: ThemeManagerImpl): ThemeManager
}
