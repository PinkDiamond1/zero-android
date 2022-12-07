package com.zero.android.datastore.migrations

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import com.zero.android.datastore.AppPreferences
import com.zero.android.datastore.AppPreferences.Companion.AUTH_CREDENTIALS
import com.zero.android.datastore.AppPreferences.Companion.IS_SETUP_COMPLETE

/** Adding [AppPreferences.IS_SETUP_COMPLETE] */
internal class DatastoreMigration1 : DataMigration<Preferences> {

	override suspend fun shouldMigrate(currentData: Preferences) =
		currentData[AUTH_CREDENTIALS] != null && !currentData.contains(IS_SETUP_COMPLETE)

	override suspend fun migrate(currentData: Preferences): Preferences {
		return currentData.toMutablePreferences().apply { set(IS_SETUP_COMPLETE, true) }
	}

	override suspend fun cleanUp() = Unit
}
