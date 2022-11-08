package com.zero.android.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiInviteDetail(
	val networkId: String,
	val networkName: String,
	val referrerName: String,
	val useCustomInviteFlow: Boolean,
	val isValid: Boolean
)
