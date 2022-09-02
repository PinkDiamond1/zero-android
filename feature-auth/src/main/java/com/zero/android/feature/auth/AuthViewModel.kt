package com.zero.android.feature.auth

import android.content.Context
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.manager.AuthManager
import com.zero.android.data.repository.AuthRepository
import com.zero.android.feature.auth.util.AuthUtil
import com.zero.android.models.AuthCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject
constructor(
	private val authManager: AuthManager,
	private val authRepository: AuthRepository,
	private val authUtil: AuthUtil
) : BaseViewModel() {

	enum class AuthScreenUIState {
		LOGIN,
		AUTH_REQUIRED
	}

	val uiState = MutableStateFlow(AuthScreenUIState.AUTH_REQUIRED)
	val loading = MutableStateFlow(false)
	val error = MutableStateFlow<String?>(null)

	val loginValidator = MutableStateFlow(AuthUtil.LoginValidator())
	val registerValidator = MutableStateFlow(AuthUtil.RegistrationValidator())
	val forgotPasswordValidator = MutableStateFlow(AuthUtil.ResetPasswordValidator())
	val forgotPasswordRequestState: MutableStateFlow<Boolean> = MutableStateFlow(false)

	fun login(email: String?, password: String?) {
		ioScope.launch {
			val formValidator = authUtil.validateLogin(email, password)
			if (formValidator.isDataValid) {
				loading.emit(true)
				authRepository.login(email!!, password!!).asResult().collect {
					when (it) {
						is Result.Success -> onAuth(it.data)
						is Result.Error -> {
							loading.emit(false)
							error.emit((it as? Result.Error)?.exception?.message)
						}
						else -> {}
					}
				}
			} else {
				loginValidator.emit(formValidator)
			}
		}
	}

	fun loginWithGoogle(context: Context) {
		ioScope.launch {
			loading.emit(true)
			authRepository.loginWithGoogle(context).asResult().collect {
				when (it) {
					is Result.Success -> onAuth(it.data)
					is Result.Error -> {
						loading.emit(false)
						error.emit((it as? Result.Error)?.exception?.message)
					}
					else -> {}
				}
			}
		}
	}

	fun loginWithApple(context: Context) {
		ioScope.launch {
			loading.emit(true)
			authRepository.loginWithApple(context).asResult().collect {
				when (it) {
					is Result.Success -> onAuth(it.data)
					is Result.Error -> {
						loading.emit(false)
						error.emit((it as? Result.Error)?.exception?.message)
					}
					else -> {}
				}
			}
		}
	}

	fun resetPassword(email: String?) {
		ioScope.launch {
			val formValidator = authUtil.validateForgotPassword(email)
			if (formValidator.isDataValid) {
				loading.emit(true)
				authRepository.forgotPassword(email!!).asResult().collect {
					when (it) {
						is Result.Success -> {
							loading.emit(false)
							forgotPasswordRequestState.emit(true)
						}
						is Result.Error -> {
							loading.emit(false)
							error.emit((it as? Result.Error)?.exception?.message)
						}
						else -> {}
					}
				}
			} else {
				forgotPasswordValidator.emit(formValidator)
			}
		}
	}

	fun register(
		name: String?,
		email: String?,
		password: String?,
		confirmPassword: String?,
		profilePic: File? = null
	) {
		ioScope.launch {
			val formValidator = authUtil.validateRegistration(name, email, password, confirmPassword)
			if (formValidator.isDataValid) {
				loading.emit(true)
				authRepository.register(name!!, email!!, password!!, profilePic).asResult().collect {
					when (it) {
						is Result.Success -> onAuth(it.data)
						is Result.Error -> {
							loading.emit(false)
							error.emit((it as? Result.Error)?.exception?.message)
						}
						else -> {}
					}
				}
			} else {
				registerValidator.emit(formValidator)
			}
		}
	}

	private fun onAuth(authCredentials: AuthCredentials) {
		CoroutineScope(Dispatchers.IO).launch {
			authManager.login(authCredentials)
			uiState.emit(AuthScreenUIState.LOGIN)
			loading.emit(false)
		}
	}
}
