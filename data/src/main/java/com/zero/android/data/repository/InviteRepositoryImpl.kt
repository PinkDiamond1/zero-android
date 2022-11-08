package com.zero.android.data.repository

import com.zero.android.common.extensions.callbackFlowWithAwait
import com.zero.android.data.conversion.toModel
import com.zero.android.models.Invite
import com.zero.android.models.InviteDetail
import com.zero.android.network.service.InviteService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InviteRepositoryImpl
@Inject
constructor(private val inviteService: InviteService, private val userRepository: UserRepository) :
	InviteRepository {

	override suspend fun onInvite(inviteCode: String) = callbackFlowWithAwait {
		val inviteInfo = async { inviteService.resolveInvite(inviteCode) }.await()
		async { userRepository.syncAccount(inviteInfo.id) }.await()
		trySend(inviteInfo.toModel())
	}

	override suspend fun getInviteDetails(inviteCode: String): Flow<InviteDetail> =
		callbackFlowWithAwait {
			val inviteDetails = inviteService.getInviteDetails(inviteCode)
			trySend(inviteDetails.toModel())
		}

	override suspend fun resolveInvite(inviteCode: String): Flow<Invite> = callbackFlowWithAwait {
		val invite = inviteService.resolveInvite(inviteCode)
		trySend(invite.toModel())
	}
}
