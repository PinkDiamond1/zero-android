package com.zero.android.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.zero.android.common.system.Logger
import com.zero.android.common.util.MEMBERS_PAGE_LIMIT
import com.zero.android.data.conversion.toEntity
import com.zero.android.database.dao.MemberDao
import com.zero.android.database.model.MemberEntity
import com.zero.android.network.model.request.GetMembersFilter
import com.zero.android.network.service.MemberService
import java.io.IOException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
internal class MembersRemoteMediator(
	private val networkId: String,
	private val memberService: MemberService,
	private val memberDao: MemberDao,
	private val logger: Logger
) : RemoteMediator<Int, MemberEntity>() {

	private var offset = 0

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, MemberEntity>
	): MediatorResult {
		return try {
			when (loadType) {
				LoadType.REFRESH -> offset = 0
				LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
				LoadType.APPEND -> {
					state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
					offset += 1
				}
			}

			val response =
				memberService.getByNetwork(
					networkId,
					GetMembersFilter(limit = MEMBERS_PAGE_LIMIT, offset = offset).toString()
				)

			response?.map { it.toEntity() }?.let { memberDao.upsert(networkId, it) }

			logger.d("Loading Network Members: $loadType - $offset: ${response?.size ?: 0}")

			MediatorResult.Success(
				endOfPaginationReached = response.isNullOrEmpty() || response.size < MEMBERS_PAGE_LIMIT
			)
		} catch (e: UnknownHostException) {
			MediatorResult.Error(e)
		} catch (e: IOException) {
			MediatorResult.Error(e)
		} catch (e: Exception) {
			logger.e(e)
			MediatorResult.Error(e)
		}
	}

	override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH
}
