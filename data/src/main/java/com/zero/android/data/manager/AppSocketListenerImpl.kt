package com.zero.android.data.manager

import com.zero.android.common.extensions.withScope
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.database.dao.ChannelDao
import com.zero.android.database.dao.MessageDao
import com.zero.android.models.enums.ChannelType
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.network.SocketListener
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel
import com.zero.android.network.model.ApiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class AppSocketListenerImpl
@Inject
constructor(
	private val preferences: Preferences,
	private val messageDao: MessageDao,
	private val channelDao: ChannelDao,
	private val channelRepository: ChannelRepository
) : SocketListener {

	val userId = runBlocking(Dispatchers.IO) { preferences.userId() }

	private suspend fun updateChannel(channel: ApiChannel) {
		if (channel is ApiDirectChannel) channelDao.upsert(channel.toEntity(userId))
		else if (channel is ApiGroupChannel) channelDao.upsert(channel.toEntity())
	}

	override fun onChannelChanged(channel: ApiChannel) {
		withScope(Dispatchers.IO) { updateChannel(channel) }
	}

	override fun onChannelDeleted(id: String, channelType: ChannelType) {
		withScope(Dispatchers.IO) { channelDao.delete(id) }
	}

	override fun onReadReceiptUpdated(channel: ApiChannel) {
		onChannelChanged(channel)
		withScope(Dispatchers.IO) { updateMessagesStatus(channel, DeliveryStatus.READ) }
	}

	override fun onOperatorUpdated(channel: ApiChannel) = onChannelChanged(channel)

	override fun onDeliveryReceiptUpdated(channel: ApiChannel) {
		onChannelChanged(channel)
		withScope(Dispatchers.IO) { updateMessagesStatus(channel, DeliveryStatus.DELIVERED) }
	}

	override fun onMessageReceived(channel: ApiChannel, message: ApiMessage) {
		withScope(Dispatchers.IO) {
			updateChannel(channel)
			messageDao.upsert(message.toEntity(), verifyChannel = true)
		}
	}

	override fun onMessageUpdated(channel: ApiChannel, message: ApiMessage) {
		withScope(Dispatchers.IO) {
			updateChannel(channel)
			messageDao.upsert(message.toEntity(), verifyChannel = true)
		}
	}

	override fun onMessageDeleted(channel: ApiChannel, msgId: String) {
		withScope(Dispatchers.IO) { messageDao.delete(msgId) }
	}

	override fun onMentionReceived(channel: ApiChannel, message: ApiMessage) {
		withScope(Dispatchers.IO) {
			updateChannel(channel)
			messageDao.upsert(message.toEntity(), verifyChannel = true)
		}
	}

	private suspend fun updateMessagesStatus(channel: ApiChannel, deliveryStatus: DeliveryStatus) {
		val loggedInUser = preferences.userId()
		channel.lastMessage?.let { message ->
			if (message.author?.id == loggedInUser && channel.memberCount == 2) {
				val chatMembers = channel.members.filter { it.id != loggedInUser }.map { it.id }
				val readMembers = channelRepository.getReadMembers(channel.id).map { it.id }
				if (readMembers.containsAll(chatMembers)) {
					messageDao.updateDeliveryReceipt(channel.id, deliveryStatus)
				}
			}
		}
	}
}
