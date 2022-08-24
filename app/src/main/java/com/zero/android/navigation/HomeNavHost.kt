package com.zero.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.models.Network

@Composable
fun HomeNavHost(
	bottomNavController: NavHostController,
	navController: NavController,
	network: Network?
) {
	NavHost(navController = bottomNavController, startDestination = ChannelsDestination.route) {
		homeBottomNavGraph(navController, network = network)
	}
}
