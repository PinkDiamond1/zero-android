package com.zero.android.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.ChannelWithRefs
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DirectChannelDaoImpl : BaseChannelDao() {

	@Transaction
	@Query(
		"""
		SELECT * FROM channels
		WHERE isDirectChannel = 1 
		AND lastMessage IS NOT NULL 
		ORDER BY lastMessageTime DESC
		"""
	)
	abstract fun getAll(): PagingSource<Int, ChannelWithRefs>

	@Transaction
	@Query(
		"""
		SELECT * FROM channels 
		WHERE isDirectChannel = 1 
		AND lastMessage IS NOT NULL 
		AND name LIKE '%'||:name||'%'
		"""
	)
	abstract fun search(name: String): PagingSource<Int, ChannelWithRefs>

	@Transaction
	@Query("SELECT SUM(unreadMessageCount) as count FROM channels WHERE isDirectChannel = 1")
	abstract fun getUnreadCount(): Flow<Int>
}
