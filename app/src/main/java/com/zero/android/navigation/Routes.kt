package com.zero.android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.zero.android.feature.account.navigation.NotificationsDestination
import com.zero.android.feature.account.ui.notifications.NotificationsRoute
import com.zero.android.feature.auth.navigation.AuthDestination
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
import com.zero.android.navigation.extensions.composable

internal fun NavGraphBuilder.onboardGraph(controller: NavController) {
	authGraph(onLogin = { controller.navigate(HomeDestination.route) { popUpTo(0) } })
	homeGraph(onLogout = { controller.navigate(AuthDestination.route) { popUpTo(0) } })
}

internal fun NavGraphBuilder.appBottomNavGraph(controller: NavController, network: Network?) {
	composable(ChannelsDestination) {
		ChannelsRoute(network = network) {
			controller.navigate(route = MessagesDestination.route(it.id, true))
		}
	}
	composable(DirectChannelDestination) {
		DirectChannelsRoute { controller.navigate(route = MessagesDestination.route(it.id, false)) }
	}
	composable(MembersDestination) { MembersRoute() }
	composable(FeedDestination) { FeedRoute() }
	composable(NotificationsDestination) { NotificationsRoute() }
	chatGraph(onBackClick = { controller.navigateUp() })
}
