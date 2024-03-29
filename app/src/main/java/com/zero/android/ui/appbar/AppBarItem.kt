package com.zero.android.ui.appbar

import com.zero.android.navigation.NavDestination

data class AppBarItem(
	val destination: NavDestination,
	val selectedIcon: Int,
	val unselectedIcon: Int
)
