package com.zero.android.data.repository

import com.zero.android.database.dao.MemberDao
import com.zero.android.network.model.toMember
import com.zero.android.network.service.MemberService
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class MemberRepositoryImpl
@Inject
constructor(private val memberDao: MemberDao, private val memberService: MemberService) :
	MemberRepository {

	// TODO: Use MemberDao like in `ChannelRepositoryImpl.getDirectChannel()`
	override suspend fun getMembers(filter: String) = flow {
		val filterObject = JSONObject().apply { putOpt("filter", filter) }
		val networkUsers = memberService.getMembers(filterObject.toString())
		emit(networkUsers.body()?.map { it.toMember() } ?: emptyList())
	}
}
