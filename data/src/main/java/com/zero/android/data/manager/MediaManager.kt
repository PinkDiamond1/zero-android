package com.zero.android.data.manager

import com.zero.android.models.ChatMedia
import com.zero.android.models.Message

interface MediaManager {

	suspend fun download(media: ChatMedia)

	suspend fun copyToClipboard(message: Message)
}
