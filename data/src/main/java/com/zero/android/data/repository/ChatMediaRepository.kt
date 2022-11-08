package com.zero.android.data.repository

import com.zero.android.models.ChatMedia
import kotlinx.coroutines.flow.Flow

interface ChatMediaRepository {
	val chatMedia: Flow<List<ChatMedia>>

	suspend fun getChatMedia(channelId: String)
}
