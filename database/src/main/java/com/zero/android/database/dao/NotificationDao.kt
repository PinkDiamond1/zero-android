package com.zero.android.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NotificationDao : BaseDao<NotificationEntity>() {

	@Transaction
	@Query("SELECT * FROM notifications WHERE id = :id")
	internal abstract fun get(id: String): Flow<NotificationEntity?>

	@Transaction
	@Query("SELECT * FROM notifications ORDER BY createdAt DESC")
	abstract fun getAll(): PagingSource<Int, NotificationEntity>

	@Transaction
	@Query("SELECT * FROM notifications WHERE networkId = :networkId ORDER BY createdAt DESC")
	abstract fun getByNetwork(networkId: String): PagingSource<Int, NotificationEntity>

	@Transaction
	@Query("SELECT * FROM notifications WHERE channelId = :channelId ORDER BY createdAt DESC")
	abstract fun getByChannel(channelId: String): PagingSource<Int, NotificationEntity>
}
