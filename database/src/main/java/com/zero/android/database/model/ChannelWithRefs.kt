package com.zero.android.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ChannelWithRefs(
	@Embedded val channel: ChannelEntity,
	@Relation(parentColumn = "authorId", entityColumn = "id") val createdBy: MemberEntity? = null,
	@Relation(
		parentColumn = "id",
		entityColumn = "id",
		associateBy =
		Junction(
			value = ChannelMembersCrossRef::class,
			parentColumn = "channelId",
			entityColumn = "memberId"
		)
	)
	val members: List<MemberEntity>,
	@Relation(
		parentColumn = "id",
		entityColumn = "id",
		associateBy =
		Junction(
			value = ChannelOperatorsCrossRef::class,
			parentColumn = "channelId",
			entityColumn = "memberId"
		)
	)
	val operators: List<MemberEntity>
)

fun ChannelWithRefs.toDirectModel() =
	channel.toDirectModel(
		members = members.map { it.toModel() },
		operators = operators.map { it.toModel() },
		lastMessage = channel.lastMessage
	)

fun ChannelWithRefs.toGroupModel() =
	channel.toGroupModel(
		members = members.map { it.toModel() },
		operators = operators.map { it.toModel() },
		lastMessage = channel.lastMessage,
		createdBy = createdBy?.toModel()
	)
