package com.zero.android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.zero.android.navigation.extensions.composable
import com.zero.android.ui.home.HomeRoute

object HomeDestination : NavDestination() {
	override val route = "home_route"
	override val destination = "home_destination"
}

internal fun NavGraphBuilder.homeGraph(
	navController: NavController,
	onNavigateToRootDestination: (NavDestination) -> Unit,
	onLogout: () -> Unit
) {
	composable(HomeDestination) {
		HomeRoute(
			navController = navController,
			onNavigateToRootDestination = onNavigateToRootDestination,
			onLogout = onLogout
		)
	}
}
