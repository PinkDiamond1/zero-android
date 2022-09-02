package com.zero.android.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zero.android.feature.auth.ui.login.LoginRoute
import com.zero.android.feature.auth.ui.registration.RegisterRoute
import com.zero.android.feature.auth.ui.resetpassword.ResetPasswordRoute
import com.zero.android.navigation.NavDestination

object AuthDestination : NavDestination() {
	override val route = "login_route"
	override val destination = "login_destination"
}

object ForgotPasswordDestination : NavDestination() {
	override val route = "forgot_password_route"
	override val destination = "forgot_password_destination"
}

object RegisterDestination : NavDestination() {
	override val route = "register_route"
	override val destination = "register_destination"
}

fun NavGraphBuilder.authGraph(
	onLogin: () -> Unit,
	onForgotPassword: () -> Unit,
	onRegister: () -> Unit,
	onBackPress: () -> Unit
) {
	composable(route = AuthDestination.route) {
		LoginRoute(onLogin = onLogin, onForgotPassword = onForgotPassword, onRegister = onRegister)
	}
	composable(route = ForgotPasswordDestination.route) { ResetPasswordRoute(onBack = onBackPress) }
	composable(route = RegisterDestination.route) { RegisterRoute(onBack = onBackPress) }
}
