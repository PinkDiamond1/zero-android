package com.zero.android.feature.auth.util

import com.zero.android.common.R
import com.zero.android.common.util.ValidationUtil
import javax.inject.Inject

class AuthValidator @Inject constructor() {

	class LoginValidator(
		val emailError: Int? = null,
		val passwordError: Int? = null,
		val isDataValid: Boolean = false
	)

	class ResetPasswordValidator(val emailError: Int? = null, val isDataValid: Boolean = false)

	class RegistrationValidator(
		val nameError: Int? = null,
		val emailError: Int? = null,
		val passwordError: Int? = null,
		val confirmPasswordError: Int? = null,
		val isDataValid: Boolean = false
	)

	fun validateLogin(email: String?, password: String?): LoginValidator {
		val emailError = ValidationUtil.validateEmail(email)
		val passwordError = ValidationUtil.validatePassword(password, false)
		return LoginValidator(
			emailError,
			passwordError,
			isDataValid = emailError == null && passwordError == null
		)
	}

	fun validateForgotPassword(email: String?): ResetPasswordValidator {
		val emailError = ValidationUtil.validateEmail(email)
		return ResetPasswordValidator(emailError, isDataValid = emailError == null)
	}

	fun validateRegistration(
		name: String?,
		email: String?,
		password: String?,
		confirmPassword: String?
	): RegistrationValidator {
		val nameError = ValidationUtil.validateInput(name, R.string.name_required)
		val emailError = ValidationUtil.validateEmail(email)
		val passwordError = ValidationUtil.validatePassword(password, true)
		val confirmPasswordError =
			ValidationUtil.validateInput(confirmPassword, R.string.password_required)
				?: ValidationUtil.validatePasswordMatch(password, confirmPassword)
		return RegistrationValidator(
			nameError,
			emailError,
			passwordError,
			confirmPasswordError,
			isDataValid =
			nameError == null &&
				emailError == null &&
				passwordError == null &&
				confirmPasswordError == null
		)
	}
}
