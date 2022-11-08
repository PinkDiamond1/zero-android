package com.zero.android.database.dao

import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.MessageStatus
import com.zero.android.models.enums.MessageType
import javax.inject.Inject

class MessageDao
@Inject
constructor(
	private val messageDao: MessageDaoImpl,
	private val memberDao: MemberDao,
	private val channelDao: DirectChannelDaoImpl
) {

	fun get(id: String) = messageDao.get(id)

	fun getByChannel(channelId: String) = messageDao.getByChannel(channelId)

	fun getLatestMessageByChannel(channelId: String) = messageDao.getLatestMessageByChannel(channelId)

	suspend fun upsert(
		vararg data: MessageWithRefs,
		updateChannel: Boolean = true,
		verifyChannel: Boolean = false
	) {
		val channels = data.map { it.message.channelId }.distinct()

		if (verifyChannel) {
			val channelExists = channels.map { channelDao.exists(it) }
			data.forEach {
				val exists = channelExists[channels.indexOf(it.message.channelId)]
				if (exists) {
					messageDao.upsert(memberDao, it)
				}
			}
		} else {
			messageDao.upsert(memberDao, *data)
		}

		if (updateChannel) {
			channels.forEach { channelId ->
				getLatestMessageByChannel(channelId)?.let {
					channelDao.updateLastMessage(channelId, it.id, it.createdAt)
				}
			}
		}
	}

	suspend fun update(id: String, text: String) = messageDao.update(id, text)

	suspend fun markRead(id: String, deliveryStatus: DeliveryStatus = DeliveryStatus.READ) =
		messageDao.markRead(id, deliveryStatus)

	suspend fun updateDeliveryReceipt(channelId: String, deliveryStatus: DeliveryStatus) =
		messageDao.updateDeliveryReceipt(channelId, deliveryStatus)

	suspend fun updateStatus(id: String, status: MessageStatus) = messageDao.updateStatus(id, status)

	suspend fun delete(id: String) = messageDao.delete(id)

	suspend fun delete(message: MessageEntity) = messageDao.delete(message)

	suspend fun deleteByChannel(channelId: String) = messageDao.deleteByChannel(channelId)

	suspend fun getMediaByChannel(channelId: String) =
		messageDao.getByChannel(channelId, types = listOf(MessageType.IMAGE, MessageType.VIDEO))

	suspend fun getLastMessage(channelId: String) = messageDao.getLastMessage(channelId)
}
