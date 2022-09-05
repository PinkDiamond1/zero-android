package com.zero.android.feature.messages.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.zero.android.feature.messages.ui.messages.MessagesRoute
import com.zero.android.navigation.DeepLinks
import com.zero.android.navigation.NavDestination
import com.zero.android.ui.util.NavAnimationUtil

object MessagesDestination : NavDestination() {
	const val ARG_CHANNEL_ID = "channelId"
	const val ARG_IS_GROUP_CHANNEL = "isGroupChannel"

	private const val BASE_ROUTE = "messages_route"

	override val route = "$BASE_ROUTE/{$ARG_CHANNEL_ID}/{$ARG_IS_GROUP_CHANNEL}"
	override val destination = "messages_destination"

	override val arguments =
		listOf(
			navArgument(ARG_CHANNEL_ID) { type = NavType.StringType },
			navArgument(ARG_IS_GROUP_CHANNEL) { type = NavType.BoolType }
		)

	override val deepLinks =
		listOf(
			navDeepLink {
				uriPattern =
					"${DeepLinks.URI}/channel?channel_id={$ARG_CHANNEL_ID}&is_group={$ARG_IS_GROUP_CHANNEL}"
			}
		)

	fun route(id: String, isGroupChannel: Boolean) = "$BASE_ROUTE/$id/$isGroupChannel"
}

@ExperimentalAnimationApi
fun NavGraphBuilder.chatGraph(onBackClick: () -> Unit) {
	composable(
		route = MessagesDestination.route,
		arguments = MessagesDestination.arguments,
		enterTransition = { NavAnimationUtil.DEFAULT_ENTER_ANIM },
		exitTransition = { NavAnimationUtil.DEFAULT_EXIT_ANIM }
	) { MessagesRoute(onBackClick) }
}
