package com.zero.android.data.repository

import com.zero.android.common.extensions.channelFlowWithAwait
import com.zero.android.data.conversion.toEntity
import com.zero.android.data.conversion.toModel
import com.zero.android.database.dao.NetworkDao
import com.zero.android.database.model.toModel
import com.zero.android.datastore.AppPreferences
import com.zero.android.network.service.ChannelCategoryService
import com.zero.android.network.service.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class NetworkRepositoryImpl
@Inject
constructor(
	private val networkDao: NetworkDao,
	private val preferences: AppPreferences,
	private val networkService: NetworkService,
	private val categoryService: ChannelCategoryService
) : NetworkRepository {

	override suspend fun getNetworks() = flow {
		networkDao.getAll().firstOrNull()?.let { networks -> emit(networks.map { it.toModel() }) }

		networkService.getNetworks(preferences.userId()).let { networks ->
			networkDao.upsert(networks.map { it.toEntity() })
			emit(networks.map { it.toModel() })
		}
	}

	override suspend fun getCategories(networkId: String) = channelFlowWithAwait {
		launch(Dispatchers.Unconfined) {
			networkDao.getCategories(networkId).mapNotNull { it }.collectLatest { send(it) }
		}
		launch { categoryService.getCategories(networkId).firstOrNull()?.let { send(it) } }
	}
}
