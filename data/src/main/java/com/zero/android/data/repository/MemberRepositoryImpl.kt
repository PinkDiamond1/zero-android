package com.zero.android.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.zero.android.common.extensions.channelFlowWithAwait
import com.zero.android.common.extensions.launchSafe
import com.zero.android.common.system.Logger
import com.zero.android.common.util.INITIAL_LOAD_SIZE
import com.zero.android.common.util.MEMBERS_PAGE_LIMIT
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.delegates.Preferences
import com.zero.android.data.mediator.MembersRemoteMediator
import com.zero.android.database.dao.MemberDao
import com.zero.android.database.model.toModel
import com.zero.android.models.Member
import com.zero.android.network.model.request.GetMembersFilter
import com.zero.android.network.service.MemberService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

internal class MemberRepositoryImpl
@Inject
constructor(
	private val memberDao: MemberDao,
	private val memberService: MemberService,
	private val logger: Logger,
	preferences: Preferences
) : MemberRepository {

	private val userId = runBlocking { preferences.userId() }

	override suspend fun getMembers(filterName: String?) = channelFlowWithAwait {
		if (filterName.isNullOrEmpty()) {
			trySend(emptyList())
			return@channelFlowWithAwait
		}

		launch(Dispatchers.Unconfined) {
			memberDao
				.search(filterName)
				.mapNotNull { it?.map { member -> member.toModel() } }
				.collect { trySend(it) }
		}
		launchSafe {
			val networkUsers = memberService.getMembers(GetMembersFilter(filter = filterName).toString())
			memberDao.upsert(
				networkUsers?.filter { it.id != userId }?.map { it.toEntity() } ?: emptyList()
			)
		}
	}

	@OptIn(ExperimentalPagingApi::class)
	override suspend fun getByNetwork(id: String): Flow<PagingData<Member>> {
		return Pager(
			config =
			PagingConfig(pageSize = MEMBERS_PAGE_LIMIT, prefetchDistance = INITIAL_LOAD_SIZE),
			remoteMediator = MembersRemoteMediator(id, memberService, memberDao, logger),
			pagingSourceFactory = { memberDao.getByNetwork(id) }
		)
			.flow
			.map { data -> data.filter { it.id != userId }.map { member -> member.toModel() } }
	}
}
