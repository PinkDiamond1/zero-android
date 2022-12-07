package com.zero.android.database.dao

import com.zero.android.database.model.ChannelWithRefs
import com.zero.android.models.ChannelCategory
import javax.inject.Inject

class ChannelDao
@Inject
constructor(
	private val channelDao: GroupChannelDaoImpl,
	private val directChannelDao: DirectChannelDaoImpl,
	private val groupChannelDao: GroupChannelDaoImpl,
	private val memberDao: MemberDao,
	private val messageDao: MessageDao
) {

	fun getGroupChannels(networkId: String, category: ChannelCategory? = null) =
		if (category.isNullOrEmpty()) groupChannelDao.getByNetwork(networkId)
		else groupChannelDao.getByNetworkAndCategory(networkId, category)

	fun getDirectChannels() = directChannelDao.getAll()

	fun searchGroupChannels(networkId: String, name: String) =
		groupChannelDao.searchByNetwork(networkId, name)

	fun searchDirectChannels(name: String) = directChannelDao.search(name)

	fun getChannel(id: String) = channelDao.get(id)

	fun getUnreadDirectMessagesCount() = directChannelDao.getUnreadCount()

	suspend fun upsert(vararg data: ChannelWithRefs) = channelDao.upsert(messageDao, memberDao, *data)

	suspend fun delete(id: String) = channelDao.delete(id)
}
