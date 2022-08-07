package com.zero.android.network.chat.sendbird

import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import com.sendbird.android.OpenChannel
import com.sendbird.android.OpenChannelListQuery
import com.zero.android.common.extensions.callbackFlowWithAwait
import com.zero.android.common.extensions.withSameScope
import com.zero.android.common.system.Logger
import com.zero.android.models.Channel
import com.zero.android.models.ChannelCategory
import com.zero.android.models.DirectChannel
import com.zero.android.models.enums.ChannelType
import com.zero.android.network.chat.conversion.*
import com.zero.android.network.service.ChannelCategoryService
import com.zero.android.network.service.ChannelService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class SendBirdChannelService(private val logger: Logger) :
	SendBirdBaseService(), ChannelService, ChannelCategoryService {

	private var groupNetworkId: String? = null
	private var openNetworkId: String? = null
	private var groupQuery: GroupChannelListQuery? = null
	private var openQuery: OpenChannelListQuery? = null
	private var directQuery: GroupChannelListQuery? = null

	override suspend fun getCategories(networkId: String) =
		flow<List<ChannelCategory>> {
			getGroupChannels(networkId, ChannelType.GROUP).firstOrNull().let { channels ->
				if (channels.isNullOrEmpty()) emit(emptyList())
				else {
					emit(
						channels
							.filter { !it.category.isNullOrEmpty() }
							.map { it.category!! }
							.distinct()
							.sorted()
					)
				}
			}
		}

	override suspend fun getGroupChannels(
		networkId: String,
		type: ChannelType,
		before: String?,
		loadSize: Int,
		searchName: String?
	) = callbackFlowWithAwait {
		if (type == ChannelType.OPEN) {
			if (openNetworkId != networkId || openQuery != null) {
				openQuery =
					OpenChannel.createOpenChannelListQuery().apply {
						setLimit(100)
						setCustomTypeFilter(networkId.encodeToNetworkId())

						searchName?.let { setNameKeyword(searchName) }
					}
			}

			openQuery!!.next { channels, e ->
				if (e != null) {
					logger.e("Failed to get open channels", e)
					throw e
				}
				trySend(channels.map { it.toApi() })
			}
		} else if (type == ChannelType.GROUP) {
			if (groupNetworkId != networkId || groupQuery != null) {
				groupQuery =
					GroupChannel.createMyGroupChannelListQuery().apply {
						customTypeStartsWithFilter = networkId.encodeToNetworkId()

						isIncludeEmpty = false
						limit = 100
						memberStateFilter = GroupChannelListQuery.MemberStateFilter.ALL
						order = GroupChannelListQuery.Order.LATEST_LAST_MESSAGE

						searchName?.let { channelNameContainsFilter = searchName }
					}
			}

			groupQuery!!.next { channels, e ->
				if (e != null) {
					logger.e("Failed to get group channels", e)
					throw e
				}
				trySend(channels.map { it.toGroupApi() })
			}
		}
	}

	override suspend fun getDirectChannels(before: String?, loadSize: Int, searchName: String?) =
		callbackFlowWithAwait {
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
					throw e
				}

				trySend(channels.filter { it.networkId.isNullOrEmpty() }.map { it.toDirectApi() })
			}
		}

	override suspend fun createChannel(networkId: String, channel: Channel) = callbackFlowWithAwait {
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
					throw e
				}
				trySend(groupChannel.toApi())
			}
		} else if (channel.isOpenChannel()) {
			OpenChannel.createChannel((channel as com.zero.android.models.GroupChannel).toOpenParams()) {
					openChannel,
					e ->
				if (e != null) {
					logger.e("Failed to create channel", e)
					throw e
				}
				trySend(openChannel.toApi())
			}
		}
	}

	override suspend fun getChannel(url: String, type: ChannelType) = callbackFlowWithAwait {
		if (type == ChannelType.OPEN) {
			OpenChannel.getChannel(url) { openChannel, e ->
				if (e != null) {
					logger.e("Failed to get channel", e)
					throw e
				}
				trySend(openChannel.toApi())
			}
		} else if (type == ChannelType.GROUP) {
			GroupChannel.getChannel(url) { groupChannel, e ->
				if (e != null) {
					logger.e("Failed to get channel", e)
					throw e
				}
				trySend(groupChannel.toApi())
			}
		}
	}

	override suspend fun joinChannel(channel: Channel) =
		suspendCancellableCoroutine<Unit> { coroutine ->
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

	override suspend fun deleteChannel(channel: Channel) =
		suspendCancellableCoroutine<Unit> { coroutine ->
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
}
