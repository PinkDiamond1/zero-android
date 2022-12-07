package com.zero.android.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.MemberEntity
import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageMentionCrossRef
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.models.DraftMessage.Companion.PREFIX_DRAFT_ID
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.MessageStatus
import com.zero.android.models.enums.MessageType
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDaoImpl : BaseDao<MessageEntity>() {

	@Transaction
	@Query("SELECT * FROM messages WHERE id = :id")
	abstract fun get(id: String): Flow<MessageWithRefs?>

	@Transaction
	@Query(
		"""
		SELECT * FROM messages 
		WHERE channelId = :channelId 
		ORDER BY createdAt DESC    
		"""
	)
	abstract fun getByChannel(channelId: String): PagingSource<Int, MessageWithRefs>

	@Transaction
	@Query(
		"SELECT * FROM messages WHERE channelId = :channelId AND type IN (:types) ORDER BY createdAt DESC"
	)
	abstract fun getByChannel(
		channelId: String,
		types: List<MessageType>
	): Flow<List<MessageWithRefs>>

	@Transaction
	@Query(
		"""
		SELECT *
		FROM messages 
		WHERE channelId = :channelId 
		ORDER BY createdAt DESC LIMIT 1
		"""
	)
	internal abstract fun getLatestMessageByChannel(channelId: String): MessageWithRefs?

	@Transaction
	@Query("SELECT * FROM messages WHERE channelId = :channelId ORDER BY createdAt DESC LIMIT 1")
	abstract fun getLastMessage(channelId: String): Flow<MessageWithRefs>

	@Transaction
	internal open suspend fun upsert(memberDao: MemberDao, vararg data: MessageWithRefs) {
		for (item in data) {
			item.message.requestId?.let { deleteByRequest(item.message.requestId) }

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
	internal abstract suspend fun insert(vararg refs: MessageMentionCrossRef)

	@Query("UPDATE messages SET message = :message WHERE id = :id")
	abstract suspend fun update(id: String, message: String?)

	@Query("UPDATE messages SET status = :status WHERE id = :id")
	abstract suspend fun updateStatus(id: String, status: MessageStatus)

	@Query("UPDATE messages SET deliveryStatus = :deliveryStatus WHERE channelId = :channelId")
	abstract suspend fun updateDeliveryReceipt(channelId: String, deliveryStatus: DeliveryStatus)

	@Query("DELETE FROM messages WHERE id = :id")
	abstract suspend fun delete(id: String)

	@Transaction
	@Query("DELETE FROM messages WHERE channelId = :channelId")
	abstract suspend fun deleteByChannel(channelId: String)

	@Transaction
	@Query("DELETE FROM messages WHERE requestId = :requestId AND id LIKE '$PREFIX_DRAFT_ID%'")
	protected abstract suspend fun deleteByRequest(requestId: String)
}
