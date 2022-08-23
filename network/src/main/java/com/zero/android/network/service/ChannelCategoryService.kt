package com.zero.android.network.service

import com.zero.android.models.ChannelCategory

interface ChannelCategoryService {

	suspend fun getCategories(networkId: String): List<ChannelCategory>
}
