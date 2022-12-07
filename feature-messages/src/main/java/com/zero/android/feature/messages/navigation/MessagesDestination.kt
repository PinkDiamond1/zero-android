package com.zero.android.feature.messages.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.zero.android.feature.messages.ui.mediaviewer.MediaViewerRoute
import com.zero.android.feature.messages.ui.messages.MessagesRoute
import com.zero.android.navigation.DeepLinks
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.composable

object MessagesDestination : NavDestination() {
	const val ARG_CHANNEL_ID = "channelId"

	private const val BASE_ROUTE = "messages_route"

	override val route = "$BASE_ROUTE/{$ARG_CHANNEL_ID}"
	override val destination = "messages_destination"

	override val arguments = listOf(navArgument(ARG_CHANNEL_ID) { type = NavType.StringType })

	override val deepLinks =
		listOf(navDeepLink { uriPattern = "${DeepLinks.URI}/channel?channel_id={$ARG_CHANNEL_ID}" })

	fun route(id: String) = "$BASE_ROUTE/$id"

	fun deeplink(id: String) = "${DeepLinks.URI}/channel?channel_id=$id"
}

@ExperimentalAnimationApi
fun NavGraphBuilder.chatGraph(
	onBackClick: () -> Unit,
	onMediaClicked: (String, String) -> Unit,
	onChannelDetails: (String) -> Unit
) {
	composable(MessagesDestination) { MessagesRoute(onBackClick, onMediaClicked, onChannelDetails) }
	composable(ChatMediaViewerDestination) { MediaViewerRoute(onBackClick) }
}
