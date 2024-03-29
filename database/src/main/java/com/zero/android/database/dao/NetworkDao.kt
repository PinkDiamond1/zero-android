package com.zero.android.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.NetworkEntity
import com.zero.android.models.ChannelCategory
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NetworkDao : BaseDao<NetworkEntity>() {

	@Transaction
	@Query("SELECT * FROM networks")
	abstract fun getAll(): Flow<List<NetworkEntity>>

	@Transaction
	@Query("SELECT * FROM networks WHERE id = :id")
	abstract fun get(id: String): Flow<NetworkEntity?>

	@Query(
		"""
		SELECT DISTINCT category from channels 
		WHERE isDirectChannel = 0 
		AND networkId = :id 
		AND category IS NOT NULL AND category != '' 
		ORDER BY category
		"""
	)
	abstract fun getCategories(id: String): Flow<List<ChannelCategory>>
}
