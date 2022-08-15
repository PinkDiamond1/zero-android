package com.zero.android.common.util

import android.util.Patterns
import com.zero.android.common.R
import java.util.regex.Pattern

object ValidationUtil {
    private val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS
    private val PASSWORD_PATTERN = Pattern.compile("^" +
        "(?=.*[0-9])" +         //at least 1 digit
        "(?=.*[a-z])" +         //at least 1 lower case letter
        "(?=.*[A-Z])" +         //at least 1 upper case letter
        "(?=.*[a-zA-Z])" +      //any letter
        "(?=.*[@#$%^&+=])" +    //at least 1 special character
        "(?=\\S+$)" +           //no white spaces
        ".{8,}" +               //at least 8 characters
        "$")

    fun validateInput(
        input: String?,
        error: Int = R.string.field_required
    ): Int? {
        return when {
            input.isNullOrEmpty() -> error
            else -> null
        }
    }

    fun validateEmail(email: String?): Int? {
        return when {
            email.isNullOrEmpty() -> R.string.email_required
            !EMAIL_PATTERN.matcher(email).matches() -> R.string.email_invalid
            else -> null
        }
    }

    fun validatePassword(password: String?, checkPasswordRegex: Boolean = true): Int? {
        return if (password.isNullOrEmpty()) R.string.password_required
        else if (checkPasswordRegex && !PASSWORD_PATTERN.matcher(password).matches()) {
            R.string.password_validation
        } else null
    }

    fun validatePasswordMatch(password: String?, confirmPassword: String?): Int? {
        return if (password.equals(confirmPassword, true)) null
        else R.string.password_mismatch_error
    }
}
