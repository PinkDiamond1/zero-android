package com.zero.android.data.di

import com.zero.android.data.delegates.Preferences
import com.zero.android.data.delegates.PreferencesImpl
import com.zero.android.data.manager.*
import com.zero.android.network.SocketListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

	@Binds fun bindConnectionManager(connectionManager: ConnectionManagerImpl): ConnectionManager

	@Binds fun provideDataCleaner(dataCleaner: DataCleanerImpl): DataCleaner

	@Binds fun providePreferences(preferences: PreferencesImpl): Preferences

	@Binds fun provideAppSocketListener(socket: AppSocketListenerImpl): SocketListener

	@Binds fun provideSessionManager(preferences: SessionManagerImpl): SessionManager

	@Binds fun provideImageLoader(imageLoader: ImageLoaderImpl): ImageLoader

	@Binds fun bindMediaPlayerManager(mediaPlayerManager: MediaPlayerManagerImpl): MediaPlayerManager

	@Binds fun bindFileManager(fileManager: FileManagerImpl): FileManager

	@Binds fun bindMediaManager(chatMediaManager: MediaManagerImpl): MediaManager
}
