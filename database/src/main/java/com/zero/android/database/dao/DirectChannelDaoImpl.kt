package com.zero.android.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.ChannelMembersCrossRef
import com.zero.android.database.model.DirectChannelWithRefs
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DirectChannelDaoImpl : BaseChannelDao() {

	@Transaction
	@Query(
		"""
		SELECT * FROM channels
		WHERE isDirectChannel = 1 
		AND lastMessageId IS NOT NULL 
		ORDER BY lastMessageTime DESC
		"""
	)
	abstract fun getAll(): PagingSource<Int, DirectChannelWithRefs>

	@Transaction
	@Query(
		"""
		SELECT * FROM channels 
		WHERE isDirectChannel = 1 
		AND lastMessageId IS NOT NULL 
		AND name LIKE '%'||:name||'%'
		"""
	)
	abstract fun search(name: String): PagingSource<Int, DirectChannelWithRefs>

	@Transaction
	@Query("SELECT * FROM channels WHERE id = :id AND isDirectChannel = 1")
	abstract fun get(id: String): Flow<DirectChannelWithRefs?>

	@Transaction
	@Query("SELECT SUM(unreadMessageCount) as count FROM channels WHERE isDirectChannel = 1")
	abstract fun getUnreadCount(): Flow<Int>

	@Transaction
	internal open suspend fun insert(
		messageDao: MessageDao,
		memberDao: MemberDao,
		vararg data: DirectChannelWithRefs
	) = upsert(messageDao, memberDao, *data)

	@Transaction
	internal open suspend fun upsert(
		messageDao: MessageDao,
		memberDao: MemberDao,
		vararg data: DirectChannelWithRefs
	) {
		for (item in data) {
			upsert(item.channel)
			item.lastMessage?.let { messageDao.upsert(it, updateChannel = false) }

			memberDao.upsert(item.members)
			item.members
				.map { ChannelMembersCrossRef(channelId = item.channel.id, memberId = it.id) }
				.let { insert(*it.toTypedArray()) }
		}
	}
}
