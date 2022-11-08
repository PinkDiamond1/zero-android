package com.zero.android.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.*
import com.zero.android.navigation.extensions.composable
import com.zero.android.navigation.util.FadeNavAnimation
import com.zero.android.ui.home.HomeRoute

object HomeDestination : NavDestination() {
	const val ARG_INVITE_CODE = "inviteCode"

	private const val BASE_ROUTE = "home_route"

	override val route = "$BASE_ROUTE?$ARG_INVITE_CODE={$ARG_INVITE_CODE}"
	override val destination = "home_destination"

	override val arguments =
		listOf(
			navArgument(ARG_INVITE_CODE) {
				type = NavType.StringType
				nullable = true
				defaultValue = null
			}
		)

	fun route(inviteCode: String? = null) = "$BASE_ROUTE?$ARG_INVITE_CODE=$inviteCode"

	override val deepLinks =
		listOf(navDeepLink { uriPattern = "${DeepLinks.URI}/a/invite/{$ARG_INVITE_CODE}" })
}

@ExperimentalAnimationApi
internal fun NavGraphBuilder.homeGraph(
	navController: NavController,
	onNavigateToRootDestination: (NavDestination) -> Unit,
	onLogout: (String?) -> Unit
) {
	composable(HomeDestination, animation = FadeNavAnimation) {
		HomeRoute(
			navController = navController,
			navigateToRootDestination = onNavigateToRootDestination,
			onLogout = onLogout
		)
	}
}
