package com.zero.android.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zero.android.database.model.MemberEntity
import com.zero.android.database.model.NetworkMembersCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MemberDao : BaseDao<MemberEntity>() {

	@Transaction
	@Query("SELECT * FROM members WHERE id = :id")
	abstract fun get(id: String): Flow<MemberEntity?>

	@Transaction
	@Query("SELECT * FROM members WHERE name LIKE '%'||:name||'%'")
	abstract fun search(name: String): Flow<List<MemberEntity>?>

	@Transaction
	@Query("SELECT * FROM members WHERE id IN (:ids)")
	abstract fun getAll(ids: List<String>): Flow<List<MemberEntity>?>

	@Transaction
	@Query(
		"""
		SELECT * FROM members
		WHERE id IN (SELECT memberId FROM network_members_relation WHERE networkId = :id)
		ORDER BY status DESC, lastSeenAt DESC
		"""
	)
	abstract fun getByNetwork(id: String): PagingSource<Int, MemberEntity>

	@Transaction
	open suspend fun upsert(networkId: String, members: List<MemberEntity>) {
		for (member in members) {
			upsert(member)
			insert(NetworkMembersCrossRef(networkId = networkId, memberId = member.id))
		}
	}

	@Transaction
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	internal abstract fun insert(ref: NetworkMembersCrossRef)
}
