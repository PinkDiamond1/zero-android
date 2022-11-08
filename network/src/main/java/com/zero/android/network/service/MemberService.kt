package com.zero.android.network.service

import com.zero.android.network.model.ApiNetworkMember
import com.zero.android.network.model.request.GetMembersFilter
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MemberService {

	@GET("users/searchInNetworks")
	suspend fun getMembers(
		@Query("filter") filterJson: String = GetMembersFilter().toString()
	): List<ApiNetworkMember>?

	@GET("networks/{id}/activeUsers")
	suspend fun getByNetwork(
		@Path("id") id: String,
		@Query("filter") filterJson: String
	): List<ApiNetworkMember>?
}
