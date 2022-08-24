package com.zero.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController(),
	startDestination: String = AppGraph.AUTH
) {
	NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
		appGraph(navController)
	}
}
