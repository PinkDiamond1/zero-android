package com.zero.android.feature.channels.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.zero.android.feature.channels.ui.details.ChannelDetailsRoute
import com.zero.android.feature.channels.ui.edit.EditChannelRoute
import com.zero.android.models.ChatMedia
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.composable

object ChannelsDestination : NavDestination() {
	override val route = "channels_route"
	override val destination = "channels_destination"
}

@ExperimentalAnimationApi
fun NavGraphBuilder.channelGraph(
	onEditClick: (String, Boolean) -> Unit,
	onBackClick: () -> Unit,
	onAllMediaClick: (String) -> Unit,
	onMediaClick: (String, ChatMedia) -> Unit,
	onLeaveChannel: () -> Unit
) {
	composable(EditChannelDestination) { EditChannelRoute(onBackClick = onBackClick) }
	composable(ChannelDetailsDestination) {
		ChannelDetailsRoute(
			onEditClick = onEditClick,
			onMediaClick = onMediaClick,
			onBackClick = onBackClick,
			onAllMediaClick = onAllMediaClick,
			onLeaveChannel = onLeaveChannel
		)
	}
}
