package com.zero.android.database.dao

import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.DirectChannelWithRefs
import com.zero.android.database.model.GroupChannelWithRefs
import javax.inject.Inject

class ChannelDao
@Inject
constructor(
	private val directChannelDao: DirectChannelDaoImpl,
	private val groupChannelDao: GroupChannelDaoImpl,
	private val memberDao: MemberDao,
	private val messageDao: MessageDao
) {

	fun getGroupChannels(networkId: String) = groupChannelDao.getByNetwork(networkId)

	fun getDirectChannels() = directChannelDao.getAll()

	fun searchGroupChannels(networkId: String, name: String) =
		groupChannelDao.searchByNetwork(networkId, name)

	fun searchDirectChannels(name: String) = directChannelDao.search(name)

	fun getGroupChannel(id: String) = groupChannelDao.get(id)

	fun getDirectChannel(id: String) = directChannelDao.get(id)

	suspend fun upsert(vararg data: DirectChannelWithRefs) =
		directChannelDao.upsert(messageDao, memberDao, *data)

	suspend fun upsert(vararg data: GroupChannelWithRefs) =
		groupChannelDao.upsert(messageDao, memberDao, *data)

	internal suspend fun updateLatestMessage(id: String) {
		messageDao.getLatestMessageByChannel(id)?.let { meta ->
			directChannelDao.updateLastMessage(id, meta.id, meta.createdAt)
		}
	}

	suspend fun delete(entity: ChannelEntity) = groupChannelDao.delete(entity)
}
