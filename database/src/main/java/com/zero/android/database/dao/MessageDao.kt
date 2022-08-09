package com.zero.android.database.dao

import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageWithRefs
import javax.inject.Inject

class MessageDao
@Inject
constructor(
	private val messageDao: MessageDaoImpl,
	private val memberDao: MemberDao,
	private val directChannelDao: DirectChannelDaoImpl
) {

	fun get(id: String) = messageDao.get(id)

	fun getByChannel(channelId: String) = messageDao.getByChannel(channelId)

	fun getLatestMessageByChannel(channelId: String) = messageDao.getLatestMessageByChannel(channelId)

	suspend fun upsert(vararg data: MessageWithRefs) {
		messageDao.upsert(memberDao, *data)

		data
			.map { it.message.channelId }
			.forEach { channelId ->
				getLatestMessageByChannel(channelId)?.let {
					directChannelDao.updateLastMessage(channelId, it)
				}
			}
	}

	suspend fun update(id: String, text: String) = messageDao.update(id, text)

	suspend fun delete(id: String) = messageDao.delete(id)

	suspend fun delete(message: MessageEntity) = messageDao.delete(message)

	suspend fun deleteByChannel(channelId: String) = messageDao.deleteByChannel(channelId)
}
