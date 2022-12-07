package com.zero.android.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.zero.android.data.manager.WorkManager
import com.zero.android.models.Network
import com.zero.android.sync.worker.JoinPublicChannelsWorker
import com.zero.android.sync.worker.JoinPublicChannelsWorker.Companion.KEY_NETWORK_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class WorkManagerImpl
@Inject
constructor(@ApplicationContext private val context: Context) : WorkManager {

	override fun joinPublicChannels(network: Network) {
		val request =
			OneTimeWorkRequestBuilder<JoinPublicChannelsWorker>()
				.setConstraints(
					Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
				)
				.setInputData(workDataOf(KEY_NETWORK_ID to network.id))
				.setInitialDelay(0, TimeUnit.MILLISECONDS)
				.build()

		androidx.work.WorkManager.getInstance(context).beginWith(request).enqueue()
	}

	override fun cancelAll() {
		androidx.work.WorkManager.getInstance(context).apply {
			cancelAllWorkByTag(JoinPublicChannelsWorker::class.qualifiedName!!)
		}
	}
}
