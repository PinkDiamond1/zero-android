package com.zero.android.data.manager

import com.zero.android.models.Network

interface WorkManager {

	fun joinPublicChannels(network: Network)

	fun cancelAll()
}
