package com.zero.android.network.chat.sendbird

import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import com.sendbird.android.OpenChannel
import com.sendbird.android.OpenChannelListQuery
import com.sendbird.android.PublicGroupChannelListQuery
import com.zero.android.common.extensions.withSameScope
import com.zero.android.common.system.Logger
import com.zero.android.models.Channel
import com.zero.android.models.ChannelCategory
import com.zero.android.models.DirectChannel
import com.zero.android.models.Member
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.ChannelType
import com.zero.android.network.chat.conversion.encodeToNetworkId
import com.zero.android.network.chat.conversion.isGroupChannel
import com.zero.android.network.chat.conversion.isOpenChannel
import com.zero.android.network.chat.conversion.networkId
import com.zero.android.network.chat.conversion.toApi
import com.zero.android.network.chat.conversion.toDirectApi
import com.zero.android.network.chat.conversion.toDirectParams
import com.zero.android.network.chat.conversion.toGroupApi
import com.zero.android.network.chat.conversion.toOpenParams
import com.zero.android.network.chat.conversion.toOption
import com.zero.android.network.chat.conversion.toParams
import com.zero.android.network.chat.conversion.toUpdateParams
import com.zero.android.network.extensions.parsed
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.model.ApiMember
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
					coroutine.resumeWithException(e.parsed)
				} else {
					coroutine.resume(channels.map { it.toApi() })
				}
			}
		} else if (type == ChannelType.GROUP) {
			if (refresh) groupQuery = null
			if (groupNetworkId != networkId || groupQuery != null) {
				groupQuery =
					GroupChannel.createMyGroupChannelListQuery().apply {
						this.limit = limit.coerceAtLeast(100)
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
					coroutine.resumeWithException(e.parsed)
				} else {
					coroutine.resume(channels.map { it.toGroupApi() })
				}
			}
		}
	}

	override suspend fun getDirectChannels(
		before: String?,
		limit: Int,
		searchName: String?,
		refresh: Boolean
	) = suspendCancellableCoroutine { coroutine ->
		if (refresh) directQuery = null
		if (directQuery == null) {
			directQuery =
				GroupChannel.createMyGroupChannelListQuery().apply {
					this.limit = limit.coerceAtLeast(100)
					isIncludeEmpty = false
					memberStateFilter = GroupChannelListQuery.MemberStateFilter.ALL
					order = GroupChannelListQuery.Order.LATEST_LAST_MESSAGE

					searchName?.let { channelNameContainsFilter = searchName }
				}
		}
		directQuery!!.next { channels, e ->
			if (e != null) {
				logger.e("Failed to get direct channels", e)
				coroutine.resumeWithException(e.parsed)
			} else {
				coroutine.resume(channels.filter { it.networkId.isNullOrEmpty() }.map { it.toDirectApi() })
			}
		}
	}

	override suspend fun getPublicChannels(networkId: String) =
		suspendCancellableCoroutine { coroutine ->
			val publicChannelQuery =
				GroupChannel.createPublicGroupChannelListQuery().apply {
					limit = 100
					isIncludeEmpty = true
					membershipFilter = PublicGroupChannelListQuery.MembershipFilter.ALL
					superChannelFilter = PublicGroupChannelListQuery.SuperChannelFilter.ALL
					customTypeStartsWithFilter = networkId.encodeToNetworkId()
				}

			publicChannelQuery.next { channels, e ->
				if (e != null) {
					logger.e("Failed to get group channels", e)
					coroutine.resumeWithException(e.parsed)
				} else {
					coroutine.resume(channels.map { it.toGroupApi() })
				}
			}
		}

	override suspend fun createGroupChannel(
		networkId: String,
		channel: com.zero.android.models.GroupChannel
	) = suspendCancellableCoroutine {
		if (channel.isGroupChannel()) {
			val params = channel.toParams()

			GroupChannel.createChannel(params) { groupChannel, e ->
				if (e != null) {
					logger.e("Failed to create channel", e)
					it.resumeWithException(e.parsed)
				} else {
					it.resume(groupChannel.toGroupApi())
				}
			}
		} else if (channel.isOpenChannel()) {
			OpenChannel.createChannel(channel.toOpenParams()) { openChannel, e ->
				if (e != null) {
					logger.e("Failed to create channel", e)
					it.resumeWithException(e.parsed)
				} else {
					it.resume(openChannel.toApi())
				}
			}
		}
	}

	override suspend fun createDirectChannel(members: List<Member>) = suspendCancellableCoroutine {
		GroupChannel.createChannel(toDirectParams(members)) { directChannel, e ->
			if (e != null) {
				logger.e("Failed to create channel", e)
				it.resumeWithException(e.parsed)
			} else {
				it.resume(directChannel.toDirectApi())
			}
		}
	}

	override suspend fun getChannel(url: String, type: ChannelType): ApiChannel =
		suspendCancellableCoroutine {
			if (type == ChannelType.OPEN) {
				OpenChannel.getChannel(url) { openChannel, e ->
					if (e != null) {
						logger.e("Failed to get channel", e)
						it.resumeWithException(e.parsed)
					} else {
						it.resume(openChannel.toApi())
					}
				}
			} else if (type == ChannelType.GROUP) {
				GroupChannel.getChannel(url) { groupChannel, e ->
					if (e != null) {
						logger.e("Failed to get channel", e)
						it.resumeWithException(e.parsed)
					} else {
						it.resume(groupChannel.toApi())
					}
				}
			}
		}

	override suspend fun updateChannel(channel: Channel) = suspendCoroutine { coroutine ->
		val params =
			when (channel) {
				is com.zero.android.models.GroupChannel -> channel.toUpdateParams()
				is DirectChannel -> channel.toUpdateParams()
				else -> throw IllegalStateException()
			}

		withSameScope {
			groupChannel(channel.id).updateChannel(params) { channel, e ->
				if (e == null) coroutine.resume(channel.toApi())
				else coroutine.resumeWithException(e.parsed)
			}
		}
	}

	override suspend fun addMembers(id: String, memberIds: List<String>) =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				groupChannel(id).inviteWithUserIds(memberIds) {
					if (it != null) coroutine.resumeWithException(it.parsed) else coroutine.resume(Unit)
				}
			}
		}

	override suspend fun getNotificationSettings(id: String) =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				val channel = groupChannel(id)
				coroutine.resume(channel.toApi().alerts)
			}
		}

	override suspend fun getNotificationSettingsByNetwork(networkId: String): AlertType =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				val channels = getGroupChannels(networkId, limit = 1)
				if (channels.isNotEmpty()) coroutine.resume(channels[0].alerts)
				else coroutine.resume(AlertType.DEFAULT)
			}
		}

	override suspend fun updateNotificationSettings(id: String, alertType: AlertType) =
		suspendCancellableCoroutine { coroutine ->
			withSameScope {
				groupChannel(id).setMyPushTriggerOption(alertType.toOption()) {
					if (it != null) coroutine.resumeWithException(it.parsed) else coroutine.resume(Unit)
				}
			}
		}

	override suspend fun updateNotificationSettingsByNetwork(
		networkId: String,
		alertType: AlertType
	) = suspendCancellableCoroutine { coroutine ->
		withSameScope {
			getGroupChannels(networkId).forEach {
				groupChannel(it.id).setMyPushTriggerOption(alertType.toOption()) { e ->
					if (e != null) logger.e(e)
				}
			}

			coroutine.resume(Unit)
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
						coroutine.resumeWithException(it.parsed)
					}
				}
			} else if (channel.isOpenChannel()) {
				openChannel(channel.id).enter {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to join open channel", it)
						coroutine.resumeWithException(it.parsed)
					}
				}
			}
		}
	}

	override suspend fun leaveChannel(channel: Channel) = suspendCancellableCoroutine { coroutine ->
		withSameScope {
			if (channel.isGroupChannel()) {
				groupChannel(channel.id).leave {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to leave channel", it)
						coroutine.resumeWithException(it.parsed)
					}
				}
			} else if (channel.isOpenChannel()) {
				openChannel(channel.id).exit {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to leave open channel", it)
						coroutine.resumeWithException(it.parsed)
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
						coroutine.resumeWithException(it.parsed)
					}
				}
			} else if (channel.isOpenChannel()) {
				openChannel(channel.id).delete {
					if (it == null) {
						coroutine.resume(Unit)
					} else {
						logger.e("Failed to join channel", it)
						coroutine.resumeWithException(it.parsed)
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
							coroutine.resumeWithException(it.parsed)
						}
					}
				} else {
					coroutine.resumeWithException(IllegalStateException("Cannot mark open channel read"))
				}
			}
		}

	@Suppress("RemoveExplicitTypeArguments")
	override suspend fun getReadMembers(id: String) = suspendCancellableCoroutine { coroutine ->
		withSameScope {
			val baseChannel = groupChannel(id)
			val lastMessage = baseChannel.lastMessage
			lastMessage?.let {
				val readMembers = baseChannel.getReadMembers(lastMessage, true).map { it.toApi() }
				coroutine.resume(readMembers)
			}
				?: coroutine.resume(emptyList<ApiMember>())
		}
	}
}
