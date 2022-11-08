package com.zero.android.feature.auth

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.manager.SessionManager
import com.zero.android.data.repository.AuthRepository
import com.zero.android.data.repository.InviteRepository
import com.zero.android.feature.auth.navigation.AuthDestination
import com.zero.android.feature.auth.util.AuthValidator
import com.zero.android.models.AuthCredentials
import com.zero.android.models.Invite
import com.zero.android.models.InviteDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject
constructor(
	savedStateHandle: SavedStateHandle,
	private val sessionManager: SessionManager,
	private val authRepository: AuthRepository,
	private val inviteRepository: InviteRepository,
	private val authValidator: AuthValidator
) : BaseViewModel() {

	enum class AuthScreenUIState {
		LOGIN,
		AUTH_REQUIRED
	}

	val inviteCode: String? = savedStateHandle[AuthDestination.ARG_INVITE_CODE]
	val inviteDetails = MutableStateFlow<InviteDetail?>(null)
	private val _inviteInfo = MutableStateFlow<Invite?>(null)
	val inviteId: String
		get() = _inviteInfo.value?.id ?: ""

	val uiState = MutableStateFlow(AuthScreenUIState.AUTH_REQUIRED)
	val loading = MutableStateFlow(false)
	val error = MutableStateFlow<String?>(null)

	val loginValidator = MutableStateFlow(AuthValidator.LoginValidator())
	val registerValidator = MutableStateFlow(AuthValidator.RegistrationValidator())
	val forgotPasswordValidator = MutableStateFlow(AuthValidator.ResetPasswordValidator())
	val forgotPasswordRequestState: MutableStateFlow<Boolean> = MutableStateFlow(false)

	fun login(email: String?, password: String?) {
		ioScope.launch {
			val formValidator = authValidator.validateLogin(email, password)
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
			val formValidator = authValidator.validateForgotPassword(email)
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
			val formValidator = authValidator.validateRegistration(name, email, password, confirmPassword)
			if (formValidator.isDataValid) {
				loading.emit(true)
				authRepository
					.register(name!!, email!!, password!!, inviteCode, profilePic)
					.asResult()
					.collect {
						when (it) {
							is Result.Success -> loginAndCreateUser(email, password, name, inviteCode)
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

	private fun loginAndCreateUser(
		email: String,
		password: String,
		name: String,
		inviteCode: String?
	) {
		ioScope.launch {
			authRepository.login(email, password).asResult().collect {
				when (it) {
					is Result.Success -> {
						authRepository.createUser(it.data.accessToken, name, inviteCode)
						onAuth(it.data)
					}
					is Result.Error -> {
						loading.emit(false)
						error.emit((it as? Result.Error)?.exception?.message)
					}
					else -> {}
				}
			}
		}
	}

	private fun onAuth(authCredentials: AuthCredentials) {
		ioScope.launch {
			sessionManager.login(authCredentials)
			uiState.emit(AuthScreenUIState.LOGIN)
			loading.emit(false)
		}
	}

	fun resetErrorState(resetValidations: Boolean = false) {
		ioScope.launch {
			error.emit(null)
			if (resetValidations) {
				loginValidator.emit(AuthValidator.LoginValidator())
				forgotPasswordValidator.emit(AuthValidator.ResetPasswordValidator())
				registerValidator.emit(AuthValidator.RegistrationValidator())
			}
		}
	}

	fun checkInviteCode() {
		inviteCode?.let { invite ->
			ioScope.launch {
				loading.tryEmit(true)
				inviteRepository.resolveInvite(invite).asResult().collect { inviteInfo ->
					if (inviteInfo is Result.Success) {
						this@AuthViewModel._inviteInfo.emit(inviteInfo.data)
						inviteRepository.getInviteDetails(inviteInfo.data.id).asResult().collect { detail ->
							if (detail is Result.Success) {
								inviteDetails.emit(detail.data)
							}
							loading.emit(false)
						}
					} else {
						loading.emit(false)
					}
				}
			}
		}
	}
}
