package com.zero.android.navigation.extensions

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zero.android.navigation.NavDestination

fun NavGraphBuilder.composable(
	destination: NavDestination,
	content: @Composable (NavBackStackEntry) -> Unit
) =
	composable(
		route = destination.route,
		arguments = destination.arguments,
		deepLinks = destination.deepLinks,
		content = content
	)
