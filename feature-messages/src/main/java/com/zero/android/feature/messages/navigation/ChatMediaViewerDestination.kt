package com.zero.android.feature.messages.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.zero.android.navigation.NavDestination

object ChatMediaViewerDestination : NavDestination() {
	const val ARG_CHANNEL_ID = "channelId"
	const val ARG_MESSAGE_ID = "messageId"

	private const val BASE_ROUTE = "chat_media_viewer_route"

	override val route = "$BASE_ROUTE/{$ARG_CHANNEL_ID}/{$ARG_MESSAGE_ID}"
	override val destination = "chat_media_viewer_destination"

	override val arguments =
		listOf(
			navArgument(ARG_CHANNEL_ID) { type = NavType.StringType },
			navArgument(ARG_MESSAGE_ID) { type = NavType.StringType }
		)

	fun route(channelId: String, messageId: String) = "$BASE_ROUTE/$channelId/$messageId"
}
