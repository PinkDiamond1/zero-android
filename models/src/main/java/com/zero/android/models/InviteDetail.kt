package com.zero.android.models

data class InviteDetail(
	val networkId: String,
	val networkName: String,
	val referrerName: String,
	val useCustomInviteFlow: Boolean,
	val isValid: Boolean
)
