package com.zero.android.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.converter.AppJson.toJson
import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.ChannelMembersCrossRef
import com.zero.android.database.model.ChannelOperatorsCrossRef
import com.zero.android.database.model.ChannelWithRefs
import com.zero.android.models.MessageMeta
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BaseChannelDao : BaseDao<ChannelEntity>() {

	@Transaction
	@Query("SELECT * FROM channels WHERE id = :id")
	abstract fun get(id: String): Flow<ChannelWithRefs?>

	@Transaction
	internal open suspend fun insert(
		messageDao: MessageDao,
		memberDao: MemberDao,
		vararg data: ChannelWithRefs
	) = upsert(messageDao, memberDao, *data)

	@Transaction
	internal open suspend fun upsert(
		messageDao: MessageDao,
		memberDao: MemberDao,
		vararg data: ChannelWithRefs
	) {
		try {
			for (item in data) {
				val members = item.members.toMutableList()
				item.createdBy?.let { members.add(it) }
				memberDao.upsert(members)

				upsert(item.channel)

				item.members
					.map { ChannelMembersCrossRef(channelId = item.channel.id, memberId = it.id) }
					.let { insert(*it.toTypedArray()) }

				item.operators
					.map { ChannelOperatorsCrossRef(channelId = item.channel.id, memberId = it.id) }
					.let { insert(*it.toTypedArray()) }
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	internal abstract suspend fun insert(vararg refs: ChannelMembersCrossRef)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	internal abstract suspend fun insert(vararg refs: ChannelOperatorsCrossRef)

	@Query(
		"""
		UPDATE channels 
		SET lastMessage = :lastMessage, lastMessageTime = :lastMessageTimestamp
		WHERE id = :id
		"""
	)
	protected abstract suspend fun updateLastMessage(
		id: String,
		lastMessage: String?,
		lastMessageTimestamp: Long
	)

	internal suspend fun updateLastMessage(id: String, message: MessageMeta) =
		updateLastMessage(
			id = id,
			lastMessage = message.toJson(),
			lastMessageTimestamp = message.createdAt
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
