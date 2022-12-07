package com.zero.android.network.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiNetworkMember(
	val id: String,
	@SerialName("profileSummary") val profile: ApiProfile? = null,
	val profileId: String? = null,
	@SerialName("name") private val _name: String? = null,
	private val profileImage: String? = null,
	val type: String? = null,
	val isOnline: Boolean = false,
	val lastActiveAt: Instant? = null,
	@SerialName("createdAt") private val createdAt: Instant? = null,
	@SerialName("updatedAt") private val updatedAt: Instant? = null,
	val isAdmin: Boolean = false,
	val isAssistantAdmin: Boolean = false,
	val summary: String? = null,
	val handle: String? = null
) {
	val firstName
		get() = profile?.firstName ?: ""
	val name
		get() = _name ?: profile?.run { "$firstName $lastName" }

	val image
		get() = profileImage ?: profile?.profileImage
}
