package com.zero.android.feature.account.navigation

import androidx.navigation.NavGraphBuilder
import com.zero.android.feature.account.ui.profile.ProfileRoute
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.composable

object ProfileDestination : NavDestination() {
	override val route = "profile_route"
	override val destination = "profile_destination"
}

fun NavGraphBuilder.profileGraph() {
	composable(ProfileDestination) { ProfileRoute() }
}
