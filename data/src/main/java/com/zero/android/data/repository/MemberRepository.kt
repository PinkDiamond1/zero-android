package com.zero.android.data.repository

import androidx.paging.PagingData
import com.zero.android.models.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

	suspend fun getMembers(filterName: String? = null): Flow<List<Member>>

	suspend fun getByNetwork(id: String): Flow<PagingData<Member>>
}
