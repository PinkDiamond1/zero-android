package com.zero.android.data.conversion

import com.zero.android.database.model.MemberEntity
import com.zero.android.models.Member
import com.zero.android.models.enums.ConnectionStatus
import com.zero.android.network.model.ApiMember
import com.zero.android.network.model.ApiNetworkMember
import org.json.JSONObject

internal fun ApiMember.toModel() =
	Member(
		id = id,
		name = nickname,
		profileJson = profileJson,
		profileImage = profileImage,
		friendDiscoveryKey = friendDiscoveryKey,
		friendName = friendName,
		metadata = metadata,
		status = status,
		lastSeenAt = lastSeenAt,
		isActive = isActive,
		isBlockingMe = isBlockingMe,
		isBlockedByMe = isBlockedByMe,
		isMuted = isMuted
	)

internal fun ApiMember.toEntity() =
	MemberEntity(
		id = id,
		name = nickname,
		profileJson = profileJson,
		profileImage = profileImage,
		friendDiscoveryKey = friendDiscoveryKey,
		friendName = friendName,
		metadata = metadata,
		status = status,
		lastSeenAt = lastSeenAt,
		isActive = isActive,
		isBlockingMe = isBlockingMe,
		isBlockedByMe = isBlockedByMe,
		isMuted = isMuted
	)

internal fun ApiNetworkMember.toEntity() =
	MemberEntity(
		id = id,
		name = name,
		profileImage = profileImage,
		status = if (isOnline) ConnectionStatus.ONLINE else ConnectionStatus.OFFLINE,
		lastSeenAt = lastActiveAt?.toEpochMilliseconds() ?: 0,
		profileJson = JSONObject().apply { put("id", profileId) }.toString()
	)
