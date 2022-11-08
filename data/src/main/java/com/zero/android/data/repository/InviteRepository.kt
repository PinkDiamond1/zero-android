package com.zero.android.data.repository

import com.zero.android.models.Invite
import com.zero.android.models.InviteDetail
import kotlinx.coroutines.flow.Flow

interface InviteRepository {
	suspend fun onInvite(inviteCode: String): Flow<Invite>

	suspend fun getInviteDetails(inviteCode: String): Flow<InviteDetail>

	suspend fun resolveInvite(inviteCode: String): Flow<Invite>
}
