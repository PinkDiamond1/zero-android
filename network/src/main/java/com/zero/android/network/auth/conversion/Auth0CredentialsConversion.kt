package com.zero.android.network.auth.conversion

import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.result.Credentials
import com.zero.android.models.AuthCredentials
import kotlinx.datetime.Instant

internal fun Credentials.toAuthCredentials() =
	AuthCredentials(
		idToken = idToken,
		accessToken = accessToken,
		type = type,
		refreshToken = refreshToken,
		expiresAt = Instant.fromEpochMilliseconds(expiresAt.time)
	)

internal fun AuthenticationException.toException(): Exception =
	IllegalStateException(this.getDescription())
