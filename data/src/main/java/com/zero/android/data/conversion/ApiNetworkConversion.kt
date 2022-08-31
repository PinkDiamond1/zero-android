package com.zero.android.data.conversion

import com.zero.android.database.model.NetworkEntity
import com.zero.android.models.Network
import com.zero.android.models.NetworkPermissions
import com.zero.android.models.enums.AlertType
import com.zero.android.network.model.ApiNetwork
import com.zero.android.network.model.ApiNetworkPermissions

internal fun ApiNetwork.toModel(alerts: AlertType = AlertType.DEFAULT) =
	Network(
		id = id,
		name = name,
		displayName = displayName,
		logo = logo,
		backgroundImageUrl = backgroundImageUrl,
		lightModeBackgroundImageUrl = lightModeBackgroundImageUrl,
		isPublic = isPublic,
		locationShareType = locationShareType,
		disabledApps = disabledApps,
		inviteMode = inviteMode,
		permissions = permissions?.toModel(),
		alerts = alerts
	)

internal fun ApiNetwork.toEntity(alerts: AlertType = AlertType.DEFAULT) =
	NetworkEntity(
		id = id,
		name = name,
		displayName = displayName,
		logo = logo,
		backgroundImageUrl = backgroundImageUrl,
		lightModeBackgroundImageUrl = lightModeBackgroundImageUrl,
		isPublic = isPublic,
		locationShareType = locationShareType,
		disabledApps = disabledApps,
		inviteMode = inviteMode,
		permissions = permissions?.toModel(),
		alerts = alerts
	)

internal fun ApiNetworkPermissions.toModel() = NetworkPermissions(invite = invite)
