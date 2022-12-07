package com.zero.android.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zero.android.models.AuthCredentials
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Singleton
class AppPreferences(private val dataStore: DataStore<Preferences>) {

	internal companion object {
		internal val USER_ID = stringPreferencesKey("USER_ID")
		internal val USER_IMAGE = stringPreferencesKey("USER_IMAGE")
		internal val AUTH_CREDENTIALS = stringPreferencesKey("AUTH_CREDENTIALS")
		internal val IS_SETUP_COMPLETE = booleanPreferencesKey("IS_SETUP_COMPLETE")
	}

	suspend fun token() = authCredentials()?.accessToken

	suspend fun authCredentials(): AuthCredentials? {
		val json: String =
			dataStore.data.map { preferences -> preferences[AUTH_CREDENTIALS] }.firstOrNull()
				?: return null
		return Json.decodeFromString<AuthCredentials>(json)
	}

	suspend fun setAuthCredentials(credentials: AuthCredentials) {
		dataStore.edit { preferences ->
			preferences[AUTH_CREDENTIALS] = Json.encodeToString(credentials)
		}
	}

	suspend fun userId() = dataStore.data.map { preferences -> preferences[USER_ID] }.first() ?: ""

	suspend fun userImage() =
		dataStore.data.map { preferences -> preferences[USER_IMAGE] }.first() ?: ""

	suspend fun setUserId(id: String) {
		dataStore.edit { preferences -> preferences[USER_ID] = id }
	}

	suspend fun setUserImage(profileImage: String?) {
		profileImage?.let { imageUrl ->
			dataStore.edit { preferences -> preferences[USER_IMAGE] = imageUrl }
		}
	}

	suspend fun isSetupComplete() =
		dataStore.data.map { preferences -> preferences[IS_SETUP_COMPLETE] }.first() ?: false

	suspend fun setSetupComplete() {
		dataStore.edit { preferences -> preferences[IS_SETUP_COMPLETE] = true }
	}
}
