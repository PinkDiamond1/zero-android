package com.zero.android.data.conversion

import com.zero.android.database.model.MemberEntity
import com.zero.android.models.Member

internal fun Member.toEntity() =
	MemberEntity(
		id = id,
		name = name,
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
