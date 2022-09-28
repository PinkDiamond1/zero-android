package com.zero.android.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.navigation
import com.zero.android.feature.account.navigation.NotificationsDestination
import com.zero.android.feature.account.ui.notifications.NotificationsRoute
import com.zero.android.feature.auth.navigation.AuthDestination
import com.zero.android.feature.auth.navigation.ForgotPasswordDestination
import com.zero.android.feature.auth.navigation.RegisterDestination
import com.zero.android.feature.auth.navigation.authGraph
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.feature.channels.navigation.CreateDirectChannelDestination
import com.zero.android.feature.channels.navigation.DirectChannelsDestination
import com.zero.android.feature.channels.ui.channels.ChannelsRoute
import com.zero.android.feature.channels.ui.createdirectchannel.CreateDirectChannelRoute
import com.zero.android.feature.channels.ui.directchannels.DirectChannelsRoute
import com.zero.android.feature.feed.FeedRoute
import com.zero.android.feature.feed.navigation.FeedDestination
import com.zero.android.feature.messages.navigation.ChatMediaViewerDestination
import com.zero.android.feature.messages.navigation.MessagesDestination
import com.zero.android.feature.messages.navigation.chatGraph
import com.zero.android.feature.people.MembersRoute
import com.zero.android.feature.people.navigation.MembersDestination
import com.zero.android.models.Network
import com.zero.android.navigation.extensions.asRoot
import com.zero.android.navigation.extensions.composable
import com.zero.android.navigation.extensions.composableSimple
import com.zero.android.navigation.extensions.navigate

@ExperimentalAnimationApi
internal fun NavGraphBuilder.appGraph(controller: NavController) {
	navigation(startDestination = AuthDestination.route, route = AppGraph.AUTH) {
		authGraph(
			onLogin = { controller.navigate(HomeDestination) { asRoot() } },
			onForgotPassword = { controller.navigate(ForgotPasswordDestination) },
			onRegister = { controller.navigate(RegisterDestination) },
			onBackPress = { controller.navigateUp() }
		)
	}
	navigation(startDestination = HomeDestination.route, route = AppGraph.MAIN) {
		chatGraph(
			onBackClick = { controller.navigateUp() },
			onMediaClicked = { channel, message ->
				controller.navigate(ChatMediaViewerDestination.route(channel, message))
			}
		)
		composable(MembersDestination) { MembersRoute() }
		composable(FeedDestination) { FeedRoute() }
		composable(NotificationsDestination) { NotificationsRoute() }
		composable(CreateDirectChannelDestination) {
			CreateDirectChannelRoute(
				onChannelCreated = {
					controller.navigateUp()
					controller.navigate(MessagesDestination.route(it.id, false))
				},
				onBackClick = { controller.navigateUp() }
			)
		}

		homeGraph(
			navController = controller,
			onNavigateToRootDestination = { controller.navigate(it) },
			onLogout = { controller.navigate(AppGraph.AUTH) { asRoot() } }
		)
	}
}

internal fun NavGraphBuilder.homeBottomNavGraph(controller: NavController, network: Network?) {
	composableSimple(ChannelsDestination) {
		ChannelsRoute(network = network) { controller.navigate(MessagesDestination.route(it.id, true)) }
	}
	composableSimple(DirectChannelsDestination) {
		DirectChannelsRoute { controller.navigate(MessagesDestination.route(it.id, false)) }
	}
}
