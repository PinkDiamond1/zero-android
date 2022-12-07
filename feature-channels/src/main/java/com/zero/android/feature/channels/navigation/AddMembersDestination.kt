package com.zero.android.feature.channels.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.zero.android.navigation.NavDestination

object AddMembersDestination : NavDestination() {
	const val ARG_CHANNEL_ID = "channelId"

	private const val BASE_ROUTE = "add_members_route"

	override val route = "$BASE_ROUTE/{$ARG_CHANNEL_ID}"
	override val destination = "add_members_destination"

	override val arguments = listOf(navArgument(ARG_CHANNEL_ID) { type = NavType.StringType })

	fun route(id: String) = "$BASE_ROUTE/$id"
}
