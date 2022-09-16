package com.zero.android.data.repository

import com.zero.android.models.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

	suspend fun getMembers(filter: String): Flow<List<Member>>
}
