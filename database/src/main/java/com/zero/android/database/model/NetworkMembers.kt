package com.zero.android.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(
	tableName = "network_members_relation",
	primaryKeys = ["networkId", "memberId"],
	indices = [Index("networkId"), Index("memberId")],
	foreignKeys =
	[
		ForeignKey(
			entity = NetworkEntity::class,
			parentColumns = ["id"],
			childColumns = ["networkId"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = MemberEntity::class,
			parentColumns = ["id"],
			childColumns = ["memberId"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
internal data class NetworkMembersCrossRef(val networkId: String, val memberId: String)

data class NetworkMembers(
	@Embedded val network: NetworkEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "id",
		associateBy =
		Junction(
			value = NetworkMembersCrossRef::class,
			parentColumn = "networkId",
			entityColumn = "memberId"
		)
	)
	val members: List<MemberEntity>? = null
)
