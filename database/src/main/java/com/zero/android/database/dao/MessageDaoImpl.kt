package com.zero.android.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.zero.android.database.model.*
import com.zero.android.models.enums.MessageType
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDaoImpl : BaseDao<MessageEntity>() {

	@Transaction
	@Query("SELECT * FROM messages WHERE id = :id")
	abstract fun get(id: String): Flow<MessageWithRefs?>

	@Transaction
	@Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY createdAt DESC")
	abstract fun getByChannel(channelId: String): PagingSource<Int, MessageWithRefs>

	@Transaction
	@Query(
		"SELECT * FROM messages WHERE channelId = :channelId AND type IN (:mediaTypes) ORDER BY createdAt DESC"
	)
	abstract fun getChatMediaByChannel(
		channelId: String,
		mediaTypes: List<MessageType>
	): Flow<List<MessageWithRefs>>

	@Transaction
	@Query(
		"SELECT id, createdAt FROM messages WHERE channelId = :channelId ORDER BY createdAt DESC LIMIT 1"
	)
	abstract fun getLatestMessageByChannel(channelId: String): MessageMeta?

	@Transaction
	internal open suspend fun upsert(memberDao: MemberDao, vararg data: MessageWithRefs) {
		for (item in data) {
			val members = mutableListOf<MemberEntity>()
			item.author?.let { members.add(it) }
			item.mentions?.let { members.addAll(it) }
			item.parentMessageAuthor?.let { members.add(it) }
			memberDao.upsert(members)

			item.parentMessage?.let { upsert(it) }
			upsert(item.message)

			item.mentions
				?.map { MessageMentionCrossRef(messageId = item.message.id, memberId = it.id) }
				?.let { insert(*it.toTypedArray()) }
		}
	}

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	protected abstract suspend fun insert(vararg refs: MessageMentionCrossRef)

	@Query("UPDATE messages SET message = :message WHERE id = :id")
	abstract suspend fun update(id: String, message: String?)

	@Query("DELETE FROM messages WHERE id = :id")
	abstract suspend fun delete(id: String)

	@Transaction
	@Query("DELETE FROM messages WHERE channelId = :channelId")
	abstract suspend fun deleteByChannel(channelId: String)
}
