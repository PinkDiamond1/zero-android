package com.zero.android.data.formatter

import com.zero.android.database.model.NotificationEntity
import com.zero.android.network.model.ApiNotification

internal interface NotificationParser {

	suspend fun parse(notification: ApiNotification): NotificationEntity
}
