package com.zero.android.data.repository

import com.zero.android.common.extensions.channelFlowWithAwait
import com.zero.android.data.conversion.toEntity
import com.zero.android.database.dao.NetworkDao
import com.zero.android.database.model.toModel
import com.zero.android.datastore.AppPreferences
import com.zero.android.models.ChannelCategory
import com.zero.android.models.enums.AlertType
import com.zero.android.network.service.ChannelCategoryService
import com.zero.android.network.service.ChannelService
import com.zero.android.network.service.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NetworkRepositoryImpl
@Inject
constructor(
	private val networkDao: NetworkDao,
	private val preferences: AppPreferences,
	private val networkService: NetworkService,
	private val categoryService: ChannelCategoryService,
	private val channelService: ChannelService
) : NetworkRepository {

	override suspend fun getNetworks() = channelFlowWithAwait {
		launch(Dispatchers.Unconfined) {
			networkDao.getAll().filterNotNull().collect { networks ->
				trySend(networks.map { it.toModel() })
			}
		}
		launch {
			networkService.getNetworks(preferences.userId()).let { networks ->
				val alertTypes = mutableMapOf<String, AlertType>()
				for (network in networks) {
					alertTypes[network.id] =
						channelService.getNetworkNotificationSettings(networkId = network.id)
				}
				networkDao.upsert(networks.map { it.toEntity(alertTypes[it.id] ?: AlertType.DEFAULT) })
			}
		}
	}

	override suspend fun getCategories(id: String): Flow<List<ChannelCategory>> = flow {
		networkDao.getCategories(id).firstOrNull()?.let { emit(it) }
		emit(categoryService.getCategories(id)) // TODO: remove this after data loading after login
	}

	override suspend fun updateNotificationSettings(id: String, alertType: AlertType) {
		channelService.updateNotificationSettings(id, alertType)
		networkDao.get(id).firstOrNull()?.let { networkDao.upsert(it.copy(alerts = alertType)) }
	}
}
