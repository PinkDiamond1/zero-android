package com.zero.android.network.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiNetworkMember(
	val id: String,
	val profileId: String? = null,
	val name: String,
	val profileImage: String? = null,
	val type: String? = null,
	val isOnline: Boolean = false,
	val lastActiveAt: Instant? = null,
	val isAdmin: Boolean = false,
	val isAssistantAdmin: Boolean = false,
	val summary: String? = null,
	val handle: String? = null
)
