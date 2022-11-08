package com.zero.android.data.conversion

import com.zero.android.models.Invite
import com.zero.android.models.InviteDetail
import com.zero.android.network.model.ApiInvite
import com.zero.android.network.model.ApiInviteDetail

internal fun ApiInviteDetail.toModel() =
	InviteDetail(
		networkId = networkId,
		networkName = networkName,
		referrerName = referrerName,
		useCustomInviteFlow = useCustomInviteFlow,
		isValid = isValid
	)

internal fun ApiInvite.toModel() = Invite(id = id, slug = slug)
