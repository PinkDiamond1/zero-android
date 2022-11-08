package com.zero.android.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink

typealias NavRoute = String

abstract class NavDestination {
	/**
	 * Defines a specific route this destination belongs to. Route is a String that defines the path
	 * to your composable. You can think of it as an implicit deep link that leads to a specific
	 * destination. Each destination should have a unique route.
	 */
	abstract val route: NavRoute

	/**
	 * Defines a specific destination ID. This is needed when using nested graphs via the navigation
	 * DLS, to differentiate a specific destination's route from the route of the entire nested graph
	 * it belongs to.
	 */
	abstract val destination: String

	open val arguments: List<NamedNavArgument> = emptyList()
	open val deepLinks: List<NavDeepLink> = emptyList()
}
