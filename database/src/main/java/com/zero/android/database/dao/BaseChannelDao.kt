package com.zero.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.ChannelMembersCrossRef
import com.zero.android.database.model.ChannelOperatorsCrossRef

@Dao
abstract class BaseChannelDao : BaseDao<ChannelEntity>() {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insert(vararg refs: ChannelMembersCrossRef)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insert(vararg refs: ChannelOperatorsCrossRef)

	@Query(
		"""
		UPDATE channels 
		SET lastMessageId = :lastMessageId, lastMessageTime = :lastMessageTimestamp
		WHERE id = :id
		"""
	)
	abstract suspend fun updateLastMessage(
		id: String,
		lastMessageId: String,
		lastMessageTimestamp: Long
	)

	@Query("DELETE FROM channels WHERE id = :id")
	abstract suspend fun delete(id: String)
}
