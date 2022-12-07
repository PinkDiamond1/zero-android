package com.zero.android.network.model.serializer

import com.zero.android.models.enums.NotificationType
import com.zero.android.models.enums.toNotificationType

object NotificationTypeSerializer : EnumSerializer<NotificationType>() {
	override fun String?.stringToEnum() = toNotificationType()
}
