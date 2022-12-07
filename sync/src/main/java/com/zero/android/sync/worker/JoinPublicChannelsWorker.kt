package com.zero.android.sync.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.zero.android.common.system.NotificationManager
import com.zero.android.data.manager.ConnectionManager
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.sync.util.JOIN_PUBLIC_CHANNEL_NOTIFICATION_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

@HiltWorker
internal class JoinPublicChannelsWorker
@AssistedInject
constructor(
	@Assisted context: Context,
	@Assisted params: WorkerParameters,
	private val channelRepository: ChannelRepository,
	private val notificationManager: NotificationManager,
	private val connectionManager: ConnectionManager
) : CoroutineWorker(context, params) {

	companion object {
		internal const val KEY_NETWORK_ID = "networkId"
	}

	override suspend fun doWork(): Result =
		withContext(Dispatchers.IO) {
			val networkId = inputData.getString(KEY_NETWORK_ID) ?: return@withContext Result.failure()

			try {
				connectionManager.connect()

				channelRepository.joinPublicChannels(networkId)

				Result.success()
			} catch (e: UnknownHostException) {
				Result.retry()
			} catch (e: IOException) {
				Result.retry()
			} catch (e: Exception) {
				Result.failure()
			}
		}

	override suspend fun getForegroundInfo() =
		ForegroundInfo(
			JOIN_PUBLIC_CHANNEL_NOTIFICATION_ID,
			notificationManager.createSyncNotification(JOIN_PUBLIC_CHANNEL_NOTIFICATION_ID)
		)
}
