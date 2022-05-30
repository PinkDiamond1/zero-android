package com.zero.android.feature.auth.extensions

import com.auth0.android.result.Credentials
import com.zero.android.models.AuthCredentials

fun Credentials.toAuthCredentials() =
	AuthCredentials(
		idToken = idToken,
		accessToken = accessToken,
		type = type,
		refreshToken = refreshToken,
		expiresAt = expiresAt
	)
