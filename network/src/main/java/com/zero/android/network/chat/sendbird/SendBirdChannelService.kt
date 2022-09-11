package com.zero.android.network.chat.sendbird

import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import com.sendbird.android.OpenChannel
import com.sendbird.android.OpenChannelListQuery
import com.zero.android.common.extensions.withSameScope
import com.zero.android.common.system.Logger
import com.zero.android.models.Channel
import com.zero.android.models.ChannelCategory
import com.zero.android.models.DirectChannel
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.ChannelType
import com.zero.android.network.chat.conversion.encodeToNetworkId
import com.zero.android.network.chat.conversion.isGroupChannel
import com.zero.android.network.chat.conversion.isOpenChannel
import com.zero.android.network.chat.conversion.networkId
import com.zero.android.network.chat.conversion.toApi
import com.zero.android.network.chat.conversion.toDirectApi
import com.zero.android.network.chat.conversion.toGroupApi
import com.zero.android.network.chat.conversion.toOpenParams
import com.zero.android.network.chat.conversion.toOption
import com.zero.android.network.chat.conversion.toParams
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.service.ChannelCategoryService
import com.zero.android.network.service.ChannelService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class SendBirdChannelService(private val logger: Logger) :
	SendBirdBaseService(), ChannelService, ChannelCategoryService {

	private var groupNetworkId: String? = null
	private var openNetworkId: String? = null
	private var groupQuery: GroupChannelListQuery? = null
	private var openQuery: OpenChannelListQuery? = null
	private var directQuery: GroupChannelListQuery? = null

	override suspend fun getCategories(networkId: String) =
		suspendCancellableCoroutine<List<ChannelCategory>> { coroutine ->
			val channels = runBlocking { getGroupChannels(networkId, ChannelType.GROUP) }
			if (channels.isEmpty()) coroutine.resume(emptyList())
			else {
				coroutine.resume(
					channels
						.filter { !it.category.isNullOrEmpty() }
						.map { it.category!! }
						.distinct()
						.sorted()
				)
			}
		}

	override suspend fun getGroupChannels(
		networkId: String,
		type: ChannelType,
		before: String?,
		loadSize: Int,
		limit: Int,
		searchName: String?,
		refresh: Boolean
	) = suspendCancellableCoroutine { coroutine ->
		if (type == ChannelType.OPEN) {
			if (refresh) openQuery = null
			if (openNetworkId != networkId || openQuery != null) {
				openQuery =
					OpenChannel.createOpenChannelListQuery().apply {
						setLimit(limit)
						setCustomTypeFilter(networkId.encodeToNetworkId())

						searchName?.let { setNameKeyword(searchName) }
					}
			}

			openQuery!!.next { channels, e ->
				if (e != null) {
					logger.e("Failed to get open channels", e)
					coroutine.resumeWithException(e)
				} else {
					coroutine.resume(channels.map { it.toApi() })
				}
			}
		} else if (type == ChannelType.GROUP) {
			if (refresh) groupQuery = null
			if (groupNetworkId != networkId || groupQuery != null) {
				groupQuery =
					GroupChannel.createMyGroupChannelListQuery().apply {
						this.limit = limit
						isIncludeEmpty = false
						order = GroupChannelListQuery.Order.LATEST_LAST_MESSAGE
						customTypeStartsWithFilter = networkId.encodeToNetworkId()
						memberStateFilter = GroupChannelListQuery.MemberStateFilter.ALL

						searchName?.let { channelNameContainsFilter = searchName }
					}
			}

			groupQuery!!.next { channels, e ->
				if (e != null) {
					logger.e("Failed to get group channels", e)
					coroutine.resumeWithException(e)
				} else {
					coroutine.resume(channels.map { it.toGroupApi() })
				}
			}
		}
	}

	override suspend fun getDirectChannels(
		before: String?,
		loadSize: Int,
		searchName: String?,
		refresh: Boolean
	) = suspendCancellableCoroutine { coroutine ->
		if (refresh) directQuery = null
		if (directQuery == null) {
			directQuery =
				GroupChannel.createMyGroupChannelListQuery().apply {
					isIncludeEmpty = false
					limit = 100
					memberStateFilter = GroupChannelListQuery.MemberStateFilter.ALL
					order = GroupChannelListQuery.Order.LATEST_LAST_MESSAGE

					searchName?.let { channelNameContainsFilter = searchName }
				}
		}
		directQuery!!.next { channels, e ->
			if (e != null) {
				logger.e("Failed to get direct channels", e)
				coroutine.resumeWithException(e)
			} else {
				coroutine.resume(channels.filter { it.networkId.isNullOrEmpty() }.map { it.toDirectApi() })
			}
		}
	}

	override suspend fun createChannel(networkId: String, channel: Channel): ApiChannel =
		suspendCancellableCoroutine {
			if (channel.isGroupChannel()) {
				val params =
					when (channel) {
						is DirectChannel -> channel.toParams()
						is com.zero.android.models.GroupChannel -> channel.toParams()
						else -> throw IllegalStateException()
					}

				GroupChannel.createChannel(params) { groupChannel, e ->
					if (e != null) {
						logger.e("Failed to create channel", e)
						it.resumeWithException(e)
					} else {
						it.resume(groupChannel.toApi())
					}
				}
			} else if (channel.isOpenChannel()) {
				OpenChannel.createChannel(
					(channel as com.zero.android.models.GroupChannel).toOpenParams()
				) { openChannel, e ->
					if (e != null) {
						logger.e("Failed to create channel", e)
						it.resumeWithException(e)
					} else {
						it.resume(openChannel.toApi())
					}
				}
			}
		}

	override suspend fun getChannel(url: String, type: ChannelType): ApiChannel =
		suspendCancellableCoroutine {
			if (type == ChannelType.OPEN) {
				OpenChannel.getChannel(url) { openChannel, e ->
					if (e != null) {
						logger.e("Failed to get channel", e)
						it.resumeWithException(e)
					} else {
						it.resume(openChannel.toApi())
					}
				}
			} else if (type == ChannelType.GROUP) {
				GroupChannel.getChannel(url) { groupChannel, e ->
					if (e != null) {
						logger.e("Failed to get channel", e)
						it.resumeWithException(e)
					} else {
						it.resume(groupChannel.toApi())
					}
				}
			}
		}

	override suspend fun updateChannel(channel: Channel) = suspendCoroutine { coroutine ->
		val params =
			when (channel) {
				is DirectChannel -> channel.toParams()
				is com.zero.android.models.GroupChannel -> channel.toParams()
				else -> throw IllegalStateException()
			}

		withSameScope {
			(getChannel(channel) as GroupChannel).let {
				it.updateChannel(params) { channel, e ->
					if (e == null) coroutine.resume(channel.toApi()) else coroutine.resumeWithException(e)
				}
			}
		}
	}

	override suspend fun getNetworkNotificationSettings(networkId: String): AlertType =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				val channels = getGroupChannels(networkId, limit = 1, loadSize = 1)
				if (channels.isNotEmpty()) coroutine.resume(channels[0].alerts)
				else coroutine.resume(AlertType.DEFAULT)
			}
		}

	override suspend fun updateNotificationSettings(networkId: String, alertType: AlertType) =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				getGroupChannels(networkId).forEach {
					groupChannel(it.id).setMyPushTriggerOption(alertType.toOption()) { e ->
						if (e != null) logger.e(e)
					}
				}

				coroutine.resume(Unit)
			}
		}

	override suspend fun updateNotificationSettings(channel: Channel, alertType: AlertType) =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				groupChannel(channel.id).setMyPushTriggerOption(alertType.toOption()) {
					if (it != null) coroutine.resumeWithException(it) else coroutine.resume(Unit)
				}
			}
		}

	override suspend fun joinChannel(channel: Channel) = suspendCancellableCoroutine { coroutine ->
		withSameScope {
			if (channel.isGroupChannel()) {
				val accessCode =
					when (channel) {
						is DirectChannel -> channel.accessCode
						is com.zero.android.models.GroupChannel -> channel.accessCode
						else -> throw IllegalStateException()
					}
				groupChannel(channel.id).join(accessCode) {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to join channel", it)
						coroutine.resumeWithException(it)
					}
				}
			} else if (channel.isOpenChannel()) {
				openChannel(channel.id).enter {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to join channel", it)
						coroutine.resumeWithException(it)
					}
				}
			}
		}
	}

	override suspend fun deleteChannel(channel: Channel) = suspendCancellableCoroutine { coroutine ->
		withSameScope {
			if (channel.isGroupChannel()) {
				groupChannel(channel.id).delete {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to join channel", it)
						coroutine.resumeWithException(it)
					}
				}
			} else if (channel.isOpenChannel()) {
				openChannel(channel.id).delete {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to join channel", it)
						coroutine.resumeWithException(it)
					}
				}
			}
		}
	}

	override suspend fun markChannelRead(channel: Channel) =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				if (channel.isGroupChannel()) {
					groupChannel(channel.id).markAsRead {
						if (it == null) {
							coroutine.resume(Unit)
						} else {
							logger.e("Failed to mark channel read", it)
							coroutine.resumeWithException(it)
						}
					}
				} else {
					val ex = IllegalStateException("Cannot mark open channel read")
					coroutine.resumeWithException(ex)
				}
			}
		}
}
