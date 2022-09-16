package com.zero.android.data.repository

import com.zero.android.data.delegates.Preferences
import com.zero.android.database.dao.MemberDao
import com.zero.android.network.model.toMember
import com.zero.android.network.service.MemberService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import javax.inject.Inject

class MemberRepositoryImpl
@Inject
constructor(
	private val memberDao: MemberDao,
	private val memberService: MemberService,
	preferences: Preferences
) : MemberRepository {

	private val userId = runBlocking { preferences.userId() }

	// TODO: Use MemberDao like in `ChannelRepositoryImpl.getDirectChannel()`
	override suspend fun getMembers(filter: String) = flow {
		val filterObject = JSONObject().apply { putOpt("filter", filter) }
		val networkUsers = memberService.getMembers(filterObject.toString())
		emit(networkUsers.body()?.filter { it.id != userId }?.map { it.toMember() } ?: emptyList())
	}
}
