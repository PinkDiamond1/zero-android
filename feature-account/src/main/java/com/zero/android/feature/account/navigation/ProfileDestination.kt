package com.zero.android.feature.account.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zero.android.feature.account.ui.profile.ProfileRoute
import com.zero.android.navigation.NavDestination

object ProfileDestination : NavDestination() {
	override val route = "profile_route"
	override val destination = "profile_destination"
}

fun NavGraphBuilder.profileGraph() {
	composable(route = ProfileDestination.route) { ProfileRoute() }
}
