package com.zero.android.feature.channels.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.zero.android.feature.channels.ui.details.ChannelDetailsRoute
import com.zero.android.feature.channels.ui.edit.EditChannelRoute
import com.zero.android.feature.channels.ui.members.AddMembersRoute
import com.zero.android.models.ChatMedia
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.composable

object ChannelsDestination : NavDestination() {
	override val route = "channels_route"
	override val destination = "channels_destination"
}

@ExperimentalAnimationApi
fun NavGraphBuilder.channelGraph(
	onEditClick: (String) -> Unit,
	onAddMember: (String) -> Unit,
	onBackClick: () -> Unit,
	onMediaClick: (String, ChatMedia) -> Unit,
	onLeaveChannel: () -> Unit
) {
	composable(EditChannelDestination) { EditChannelRoute(onBackClick = onBackClick) }
	composable(AddMembersDestination) {
		AddMembersRoute(onDone = onBackClick, onBackClick = onBackClick)
	}
	composable(ChannelDetailsDestination) {
		ChannelDetailsRoute(
			onEditClick = onEditClick,
			onAddMember = onAddMember,
			onMediaClick = onMediaClick,
			onBackClick = onBackClick,
			onLeaveChannel = onLeaveChannel
		)
	}
}
