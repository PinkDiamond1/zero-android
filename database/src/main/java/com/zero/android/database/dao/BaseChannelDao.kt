package com.zero.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.ChannelMembersCrossRef
import com.zero.android.database.model.ChannelOperatorsCrossRef

@Dao
abstract class BaseChannelDao : BaseDao<ChannelEntity>() {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	internal abstract suspend fun insert(vararg refs: ChannelMembersCrossRef)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	internal abstract suspend fun insert(vararg refs: ChannelOperatorsCrossRef)

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

	@Transaction
	@Query(
		"""
		SELECT CASE when count(id) == 1 then 1 else 0 end
		FROM channels WHERE id = :id    
		"""
	)
	abstract fun exists(id: String): Boolean

	@Query("DELETE FROM channels WHERE id = :id")
	abstract suspend fun delete(id: String)
}
