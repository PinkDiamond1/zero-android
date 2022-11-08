package com.zero.android.feature.channels.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.zero.android.navigation.NavDestination

object ChannelDetailsDestination : NavDestination() {
	const val ARG_CHANNEL_ID = "channelId"
	const val ARG_IS_GROUP_CHANNEL = "isGroupChannel"

	private const val BASE_ROUTE = "channel_details_route"

	override val route = "$BASE_ROUTE/{$ARG_CHANNEL_ID}/{$ARG_IS_GROUP_CHANNEL}"
	override val destination = "channel_details_destination"

	override val arguments =
		listOf(
			navArgument(ARG_CHANNEL_ID) { type = NavType.StringType },
			navArgument(ARG_IS_GROUP_CHANNEL) { type = NavType.BoolType }
		)

	fun route(id: String, isGroupChannel: Boolean) = "$BASE_ROUTE/$id/$isGroupChannel"
}
