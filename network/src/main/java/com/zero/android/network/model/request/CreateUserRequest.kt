package com.zero.android.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(val inviteCode: String, val user: UserInfo) {

	@Serializable
	data class UserInfo(val handle: String, val firstName: String, val lastName: String)
}
