package com.zero.android.data.formatter

import android.util.ArrayMap
import com.zero.android.common.extensions.nullable
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.data.repository.NetworkRepository
import com.zero.android.database.model.NotificationEntity
import com.zero.android.models.Channel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Network
import com.zero.android.models.enums.NotificationCategory
import com.zero.android.models.enums.NotificationType.*
import com.zero.android.network.model.ApiNotification
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

internal class NotificationParserImpl
@Inject
constructor(
	private val channelRepository: ChannelRepository,
	private val networkRepository: NetworkRepository
) : NotificationParser {

	private val networks: ArrayMap<String, Network?> = ArrayMap()
	private val channels: ArrayMap<String, Channel?> = ArrayMap()

	override suspend fun parse(notification: ApiNotification): NotificationEntity {
		@Suppress("NAME_SHADOWING")
		var notification = notification

		var title = ""
		var subtitle = ""
		var image = notification.originUser?.image

		when (notification.category) {
			NotificationCategory.CHAT -> {
				val channel = nullable {
					notification.data?.channelId?.let {
						channels[it]
							?: channelRepository.getChannel(it).firstOrNull()?.also { mChannel ->
								channels[it] = mChannel
							}
					}
				}
				if (channel != null) {
					if (channel is GroupChannel) {
						val network =
							networks[channel.networkId]
								?: networkRepository.getNetwork(channel.networkId).firstOrNull()?.also {
										mNetwork ->
									networks[channel.networkId] = mNetwork
								}

						if (network != null) {
							title += network.displayName
							subtitle =
								generateDescription(
									notification,
									channelName = channel.name,
									networkName = network.displayName
								)
						}
					} else {
						subtitle = generateDescription(notification, channelName = channel.name)
					}
				} else {
					subtitle = generateDescription(notification, channelName = "unknown channel")
					notification =
						notification.copy(data = notification.data?.copy(chatId = null, _channelId = null))
				}
			}
			NotificationCategory.FEED -> {
				subtitle = generateDescription(notification)
			}
			NotificationCategory.INVITE -> {
				image = notification.data?.network?.logo ?: notification.originUser?.image
				subtitle =
					generateDescription(
						notification,
						networkName = notification.data?.network?.displayName ?: ""
					)
			}
			NotificationCategory.TASK,
			NotificationCategory.NONE -> {
				subtitle = generateDescription(notification)
			}
		}

		return notification.toEntity(title = title, description = subtitle, image = image)
	}

	private fun generateDescription(
		notification: ApiNotification,
		channelName: String = "",
		networkName: String = "",
		taskName: String = ""
	): String {
		val userName = notification.originUser?.firstName
		return when (notification.type) {
			FEED_COMMENT_MENTION -> "$userName mentioned you in a post discussion."
			FEED_COMMENT_REPLY -> "$userName replied to your comment in a post discussion."
			COMMENT_ADDED_PARTICIPANT -> "$userName wrote on a post you contributed to."
			COMMENT_ADDED_OWNER -> "$userName wrote on a post you created."
			TASK_ASSIGNED -> "You were assigned the task $taskName."
			TASK_COMMENT_MENTION -> "There is a new comment on the task $taskName"
			TASK_COMMENT_ASSIGNED -> "There is a new comment on the task $taskName"
			TASK_COMMENT_CREATOR -> "There is a new comment on the task $taskName"
			TASK_COMMENT_REPLY -> "New reply on your comment on the task $taskName"
			DM_MENTION -> "You were mentioned in a direct message conversation"
			DM_REPLY -> "New reply in direct message conversation"
			GROUP_MENTION -> "$userName mentioned you in $channelName"
			GROUP_REPLY -> "$userName replied to your message in $channelName"
			NETWORK_INVITE -> "You were added to the $networkName network."
			else -> "An unknown notification has occurred."
		}
	}
}
