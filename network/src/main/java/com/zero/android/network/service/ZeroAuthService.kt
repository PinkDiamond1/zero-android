package com.zero.android.network.service

import com.zero.android.network.model.request.CreateUserRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ZeroAuthService {

	@POST(value = "accounts/create")
	suspend fun createUser(
		@Header("Authorization") accessToken: String,
		@Body payload: CreateUserRequest
	): Response<ResponseBody>
}
