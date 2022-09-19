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

	fun deeplink(id: String, isGroupChannel: Boolean) =
		"${DeepLinks.URI}/channel?channel_id=$id&is_group=$isGroupChannel"
}

object ChatMediaViewerDestination : NavDestination() {
	const val ARG_CHANNEL_ID = "channelId"
	const val ARG_MESSAGE_ID = "messageId"
	private const val BASE_ROUTE = "chat_media_viewer_route"

	override val route = "$BASE_ROUTE/{$ARG_CHANNEL_ID}/{$ARG_MESSAGE_ID}"
	override val destination = "messages_destination"

	override val arguments =
		listOf(
			navArgument(ARG_CHANNEL_ID) { type = NavType.StringType },
			navArgument(ARG_MESSAGE_ID) { type = NavType.StringType }
		)

	fun route(channelId: String, messageId: String) = "$BASE_ROUTE/$channelId/$messageId"
}

@ExperimentalAnimationApi
fun NavGraphBuilder.chatGraph(onBackClick: () -> Unit, onMediaClicked: (String, String) -> Unit) {
	composable(MessagesDestination) { MessagesRoute(onBackClick, onMediaClicked) }
	composable(ChatMediaViewerDestination) { MediaViewerRoute(onBackClick) }
}
