package com.zero.android.network.service

import com.zero.android.network.model.ApiChatMentionMember
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MemberService {

	@GET("users/searchInNetworks")
	suspend fun getMembers(
		@Query("filter") memberFilter: String
	): Response<List<ApiChatMentionMember>>
}
