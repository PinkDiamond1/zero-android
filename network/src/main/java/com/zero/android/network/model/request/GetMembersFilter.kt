package com.zero.android.network.model.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class GetMembersFilter(
	val filter: String? = null,
	val limit: Int? = null,
	val offset: Int? = null
) {
	override fun toString() = Json.encodeToString(this)
}
