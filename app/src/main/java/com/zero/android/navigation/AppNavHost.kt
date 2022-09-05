package com.zero.android.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@ExperimentalAnimationApi
@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberAnimatedNavController(),
	startDestination: String = AppGraph.AUTH
) {
	AnimatedNavHost(
		navController = navController,
		startDestination = startDestination,
		modifier = modifier
	) {
		appGraph(navController)
	}
}
