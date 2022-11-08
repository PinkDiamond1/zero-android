package com.zero.android.network.service

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AccountService {

	@FormUrlEncoded
	@POST(value = "accounts/sync")
	suspend fun syncAccount(@Field("idToken") token: String, @Field("inviteCode") inviteCode: String)
}
