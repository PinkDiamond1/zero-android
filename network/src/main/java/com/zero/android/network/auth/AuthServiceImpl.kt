package com.zero.android.network.auth

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.zero.android.common.extensions.callbackFlowWithAwait
import com.zero.android.common.system.Logger
import com.zero.android.network.BuildConfig
import com.zero.android.network.auth.conversion.toAuthCredentials
import com.zero.android.network.auth.conversion.toException
import java.io.File
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val logger: Logger
): AuthService {
    private val auth0 by lazy { Auth0(BuildConfig.AUTH0_CLIENT_ID, BuildConfig.AUTH0_DOMAIN) }
    private val authenticationClient by lazy { AuthenticationAPIClient(auth0) }
    private val lockAudience by lazy { BuildConfig.AUTH0_AUDIENCE }

    override suspend fun login(email: String, password: String) = callbackFlowWithAwait {
        authenticationClient.login(email, password, AUTH0_REALM_CONNECTION)
            .setScope(AUTH0_SCOPE)
            .setAudience(lockAudience)
            .validateClaims()
            .start(object : Callback<Credentials, AuthenticationException>{
                override fun onFailure(error: AuthenticationException) {
                    logger.e(error.getDescription())
                    close(error.toException())
                }

                override fun onSuccess(result: Credentials) {
                    trySend(result.toAuthCredentials())
                }
            })
    }

    override suspend fun forgotPassword(email: String) = callbackFlowWithAwait {
        authenticationClient.resetPassword(email, AUTH0_REALM_CONNECTION).start(
            object : Callback<Void?, AuthenticationException>{
                override fun onFailure(error: AuthenticationException) {
                    logger.e(error.getDescription())
                    close(error.toException())
                }

                override fun onSuccess(result: Void?) {
                    trySend(Unit)
                }
            }
        )
    }

    override suspend fun register(name: String, email: String, password: String, profilePic: File?) = callbackFlowWithAwait {
        val userMeta = mapOf("status" to "pending", "invite" to "TtXFrLVdmSsW")
        authenticationClient.signUp(email, password, name, AUTH0_REALM_CONNECTION, userMeta)
            .validateClaims()
            .start(
                object : Callback<Credentials, AuthenticationException>{
                    override fun onFailure(error: AuthenticationException) {
                        logger.e(error.getDescription())
                        close(error.toException())
                    }

                    override fun onSuccess(result: Credentials) {
                        trySend(result.toAuthCredentials())
                    }
                }
            )
    }

    override suspend fun loginWithGoogle(context: Context) = callbackFlowWithAwait {
        WebAuthProvider.login(auth0)
            .withConnection(AUTH0_CONNECTION_GOOGLE)
            .withScheme(APPLICATION_ID)
            .start(context, object : Callback<Credentials, AuthenticationException>{
                override fun onFailure(error: AuthenticationException) {
                    logger.e(error.getDescription())
                    close(error.toException())
                }

                override fun onSuccess(result: Credentials) {
                    trySend(result.toAuthCredentials())
                }
            })
    }

    override suspend fun loginWithApple(context: Context) = callbackFlowWithAwait {
        WebAuthProvider.login(auth0)
            .withConnection(AUTH0_CONNECTION_APPLE)
            .withScheme(APPLICATION_ID)
            .start(context, object : Callback<Credentials, AuthenticationException>{
                override fun onFailure(error: AuthenticationException) {
                    logger.e(error.getDescription())
                    close(error.toException())
                }

                override fun onSuccess(result: Credentials) {
                    trySend(result.toAuthCredentials())
                }
            })
    }

    override suspend fun revokeToken(token: String) = callbackFlowWithAwait {
        authenticationClient.revokeToken(token)
            .start(object : Callback<Void?, AuthenticationException>{
                override fun onFailure(error: AuthenticationException) {
                    logger.e(error.getDescription())
                    close(error.toException())
                }

                override fun onSuccess(result: Void?) {
                    trySend(Unit)
                }
            })
    }

    companion object {
        private const val APPLICATION_ID = "com.zero.android"
        private const val AUTH0_REALM_CONNECTION = "Username-Password-Authentication"
        private const val AUTH0_SCOPE = "openid profile offline_access"
        private const val AUTH0_CONNECTION_GOOGLE = "google-oauth2"
        private const val AUTH0_CONNECTION_APPLE = "apple"
    }
}
