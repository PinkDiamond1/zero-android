package com.zero.android.models

import com.zero.android.models.enums.MessageType

data class ChatMedia(val messageId: String, val mediaUrl: String?, val mediaType: MessageType)
