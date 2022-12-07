package com.zero.android.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.ChannelWithRefs
import com.zero.android.models.ChannelCategory

@Dao
abstract class GroupChannelDaoImpl : BaseChannelDao() {

	@Transaction
	@Query(
		"""
		SELECT * FROM channels 
		WHERE isDirectChannel = 0 
		AND networkId = :networkId 
		ORDER BY lastMessageTime DESC
		"""
	)
	abstract fun getByNetwork(networkId: String): PagingSource<Int, ChannelWithRefs>

	@Transaction
	@Query(
		"""
		SELECT * FROM channels 
		WHERE isDirectChannel = 0 
		AND networkId = :networkId 
		AND category = :category 
		ORDER BY lastMessageTime DESC
		"""
	)
	abstract fun getByNetworkAndCategory(
		networkId: String,
		category: ChannelCategory
	): PagingSource<Int, ChannelWithRefs>

	@Transaction
	@Query(
		"""
		SELECT * FROM channels 
		WHERE isDirectChannel = 0 
		AND networkId = :networkId 
		AND name LIKE '%'||:name||'%'
		"""
	)
	abstract fun searchByNetwork(networkId: String, name: String): PagingSource<Int, ChannelWithRefs>
}
