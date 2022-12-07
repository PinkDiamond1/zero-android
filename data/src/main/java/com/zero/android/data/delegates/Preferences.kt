package com.zero.android.data.delegates

import com.zero.android.models.AuthCredentials

interface Preferences {

	suspend fun userId(): String

	suspend fun userImage(): String

	suspend fun credentials(): AuthCredentials?
}
