package com.zero.android.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import com.zero.android.feature.auth.AuthRoute
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.composable

object AuthDestination : NavDestination() {
	override val route = "login_route"
	override val destination = "login_destination"
}

fun NavGraphBuilder.authGraph(onLogin: () -> Unit) {
	composable(AuthDestination) { AuthRoute(onLogin = onLogin) }
}
