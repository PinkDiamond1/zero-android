package com.zero.android.feature.auth.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.zero.android.feature.auth.ui.login.LoginRoute
import com.zero.android.feature.auth.ui.registration.RegisterRoute
import com.zero.android.feature.auth.ui.resetpassword.ResetPasswordRoute
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.composable

object AuthDestination : NavDestination() {
	const val ARG_INVITE_CODE = "inviteCode"

	private const val BASE_ROUTE = "login_route"

	override val route = "$BASE_ROUTE?$ARG_INVITE_CODE={$ARG_INVITE_CODE}"
	override val destination = "login_destination"

	override val arguments =
		listOf(
			navArgument(ARG_INVITE_CODE) {
				type = NavType.StringType
				nullable = true
				defaultValue = null
			}
		)

	fun route(inviteCode: String? = null) = "$BASE_ROUTE?$ARG_INVITE_CODE=$inviteCode"
}

object ForgotPasswordDestination : NavDestination() {
	override val route = "forgot_password_route"
	override val destination = "forgot_password_destination"
}

object RegisterDestination : NavDestination() {
	private const val BASE_ROUTE = "register_route"

	override val route =
		"$BASE_ROUTE?${AuthDestination.ARG_INVITE_CODE}={${AuthDestination.ARG_INVITE_CODE}}"
	override val destination = "register_destination"

	override val arguments =
		listOf(
			navArgument(AuthDestination.ARG_INVITE_CODE) {
				type = NavType.StringType
				nullable = true
				defaultValue = null
			}
		)

	fun route(inviteCode: String? = null) =
		"$BASE_ROUTE?${AuthDestination.ARG_INVITE_CODE}=$inviteCode"
}

@ExperimentalAnimationApi
fun NavGraphBuilder.authGraph(
	onLogin: (String?) -> Unit,
	onForgotPassword: () -> Unit,
	onRegister: (String?) -> Unit,
	onBackPress: () -> Unit
) {
	composable(AuthDestination) {
		LoginRoute(onLogin = onLogin, onForgotPassword = onForgotPassword, onRegister = onRegister)
	}
	composable(ForgotPasswordDestination) { ResetPasswordRoute(onBack = onBackPress) }
	composable(RegisterDestination) { RegisterRoute(onRegister = onLogin, onBack = onBackPress) }
}
