package com.zero.android.feature.messages.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.zero.android.feature.messages.ui.messages.MessagesRoute
import com.zero.android.navigation.DeepLinks
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.composable

object MessagesDestination : NavDestination() {
	private const val baseRoute = "messages_route"

	private const val argChannelId = "channelId"
	private const val argIsGroupChannel = "isGroupChannel"

	override val route = "$baseRoute/{$argChannelId}/{$argIsGroupChannel}"
	override val destination = "messages_destination"

	override val arguments =
		listOf(
			navArgument(argChannelId) { type = NavType.StringType },
			navArgument(argIsGroupChannel) { type = NavType.BoolType }
		)

	override val deepLinks = listOf(navDeepLink { uriPattern = DeepLinks.CHANNEL })

	fun route(id: String, isGroupChannel: Boolean) = "$baseRoute/$id/$isGroupChannel"
}

fun NavGraphBuilder.chatGraph(onBackClick: () -> Unit) {
	composable(MessagesDestination) { MessagesRoute(onBackClick) }
}
