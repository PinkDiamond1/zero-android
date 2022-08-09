package com.zero.android.network.model

import com.zero.android.models.Member
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiChatMentionMember(
	@SerialName("id") val id: String,
	@SerialName("name") val name: String,
	@SerialName("profileImage") val profileImage: String? = null,
	@SerialName("type") val type: String? = null
)

fun ApiChatMentionMember.toMember() =
	Member(id = this.id, name = this.name, profileImage = this.profileImage)
