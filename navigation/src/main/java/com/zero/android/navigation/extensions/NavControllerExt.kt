@file:Suppress("NOTHING_TO_INLINE")

package com.zero.android.navigation.extensions

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.zero.android.navigation.NavDestination

inline fun NavController.navigate(
	destination: NavDestination,
	noinline builder: NavOptionsBuilder.() -> Unit
) = navigate(destination.route, builder)

inline fun NavController.navigate(destination: NavDestination) = navigate(destination.route)
