package com.zero.android.feature.auth

import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.result.Credentials
import com.zero.android.common.system.Logger
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.manager.AuthManager
import com.zero.android.feature.auth.extensions.toAuthCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject
constructor(private val authManager: AuthManager, private val logger: Logger) : BaseViewModel() {

	enum class AuthScreenUIState {
		LOGIN,
		AUTH_REQUIRED
	}

	val uiState = MutableStateFlow(AuthScreenUIState.AUTH_REQUIRED)

	val authCallback =
		object : AuthenticationCallback() {
			override fun onAuthentication(credentials: Credentials) {
				onAuth(credentials)
			}

			override fun onError(error: AuthenticationException) = logger.e(error)
		}

	private fun onAuth(credentials: Credentials) {
		CoroutineScope(Dispatchers.IO).launch {
			authManager.login(credentials.toAuthCredentials())
			uiState.emit(AuthScreenUIState.LOGIN)
		}
	}
}
