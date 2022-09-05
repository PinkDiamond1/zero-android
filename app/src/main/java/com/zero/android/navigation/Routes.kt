package com.zero.android.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.zero.android.feature.account.navigation.NotificationsDestination
import com.zero.android.feature.account.ui.notifications.NotificationsRoute
import com.zero.android.feature.auth.navigation.AuthDestination
import com.zero.android.feature.auth.navigation.ForgotPasswordDestination
import com.zero.android.feature.auth.navigation.RegisterDestination
import com.zero.android.feature.auth.navigation.authGraph
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.feature.channels.navigation.DirectChannelDestination
import com.zero.android.feature.channels.ui.channels.ChannelsRoute
import com.zero.android.feature.channels.ui.directchannels.DirectChannelsRoute
import com.zero.android.feature.feed.FeedRoute
import com.zero.android.feature.feed.navigation.FeedDestination
import com.zero.android.feature.messages.navigation.MessagesDestination
import com.zero.android.feature.messages.navigation.chatGraph
import com.zero.android.feature.people.MembersRoute
import com.zero.android.feature.people.navigation.MembersDestination
import com.zero.android.models.Network
import com.zero.android.navigation.extensions.asRoot
import com.zero.android.navigation.extensions.composable
import com.zero.android.ui.util.NavAnimationUtil

@ExperimentalAnimationApi
internal fun NavGraphBuilder.appGraph(controller: NavController) {
	navigation(
		startDestination = AuthDestination.route,
		route = AppGraph.AUTH,
		enterTransition = { NavAnimationUtil.DEFAULT_ENTER_ANIM },
		exitTransition = { NavAnimationUtil.DEFAULT_EXIT_ANIM },
		popEnterTransition = { NavAnimationUtil.DEFAULT_POP_ENTER_ANIM },
		popExitTransition = { NavAnimationUtil.DEFAULT_POP_EXIT_ANIM }
	) {
		authGraph(
			onLogin = { controller.navigate(HomeDestination.route) { asRoot() } },
			onForgotPassword = { controller.navigate(ForgotPasswordDestination.route) },
			onRegister = { controller.navigate(RegisterDestination.route) },
			onBackPress = { controller.navigateUp() }
		)
	}
	navigation(startDestination = HomeDestination.route, route = AppGraph.MAIN) {
		chatGraph(onBackClick = { controller.navigateUp() })
		composable(MembersDestination.route) { MembersRoute() }
		composable(FeedDestination.route) { FeedRoute() }
		composable(NotificationsDestination.route) { NotificationsRoute() }

		homeGraph(
			navController = controller,
			onNavigateToRootDestination = {
				controller.navigate(it.route) { popUpTo(controller.graph.startDestinationId) }
			},
			onLogout = { controller.navigate(AppGraph.AUTH) { asRoot() } }
		)
	}
}

internal fun NavGraphBuilder.homeBottomNavGraph(controller: NavController, network: Network?) {
	composable(ChannelsDestination) {
		ChannelsRoute(network = network) { controller.navigate(MessagesDestination.route(it.id, true)) }
	}
	composable(DirectChannelDestination) {
		DirectChannelsRoute { controller.navigate(MessagesDestination.route(it.id, false)) }
	}
}
