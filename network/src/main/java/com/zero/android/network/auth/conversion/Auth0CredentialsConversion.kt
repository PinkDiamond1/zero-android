package com.zero.android.network.auth.conversion

import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.result.Credentials
import com.zero.android.models.AuthCredentials
import kotlinx.datetime.Instant
import java.lang.Exception

fun Credentials.toAuthCredentials() =
    AuthCredentials(
        idToken = idToken,
        accessToken = accessToken,
        type = type,
        refreshToken = refreshToken,
        expiresAt = Instant.fromEpochMilliseconds(expiresAt.time)
    )

fun AuthenticationException.toException(): Exception = IllegalStateException(this.getDescription())
