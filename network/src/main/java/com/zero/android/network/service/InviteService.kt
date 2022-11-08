package com.zero.android.network.service

import com.zero.android.network.model.ApiInvite
import com.zero.android.network.model.ApiInviteDetail
import retrofit2.http.GET
import retrofit2.http.Path

interface InviteService {

	@GET(value = "invites/resolve/{invite_code}")
	suspend fun resolveInvite(@Path("invite_code") inviteCode: String): ApiInvite

	@GET(value = "invites/{invite_code}/detail")
	suspend fun getInviteDetails(@Path("invite_code") inviteCode: String): ApiInviteDetail
}
