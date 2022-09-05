package com.zero.android.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.zero.android.ui.home.HomeRoute
import com.zero.android.ui.util.NavAnimationUtil

object HomeDestination : NavDestination() {
	override val route = "home_route"
	override val destination = "home_destination"
}

@ExperimentalAnimationApi
internal fun NavGraphBuilder.homeGraph(
	navController: NavController,
	onNavigateToRootDestination: (NavDestination) -> Unit,
	onLogout: () -> Unit
) {
	composable(
		route = HomeDestination.route,
		enterTransition = { NavAnimationUtil.DEFAULT_ENTER_ANIM },
		exitTransition = { NavAnimationUtil.DEFAULT_EXIT_ANIM },
		popEnterTransition = { NavAnimationUtil.DEFAULT_POP_ENTER_ANIM },
		popExitTransition = { NavAnimationUtil.DEFAULT_POP_EXIT_ANIM }
	) {
		HomeRoute(
			navController = navController,
			onNavigateToRootDestination = onNavigateToRootDestination,
			onLogout = onLogout
		)
	}
}
