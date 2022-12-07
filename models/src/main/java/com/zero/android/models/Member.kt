package com.zero.android.models

import com.zero.android.models.enums.ConnectionStatus
import kotlinx.serialization.Serializable

interface BaseMember {
	val id: String
	val name: String?
	val image: String?
}

data class Member(
	override val id: String,
	override val name: String? = null,
	var profileJson: String? = null,
	override val image: String? = null,
	var friendDiscoveryKey: String? = null,
	var friendName: String? = null,
	var metadata: Map<String, String?>? = null,
	var status: ConnectionStatus = ConnectionStatus.NON_AVAILABLE,
	var lastSeenAt: Long = 0,
	val isActive: Boolean = true,
	val isBlockingMe: Boolean = false,
	val isBlockedByMe: Boolean = false,
	val isMuted: Boolean = false
) : BaseMember

@Serializable
data class MemberMeta(
	override val id: String,
	override val name: String? = null,
	override val image: String? = null
) : BaseMember
