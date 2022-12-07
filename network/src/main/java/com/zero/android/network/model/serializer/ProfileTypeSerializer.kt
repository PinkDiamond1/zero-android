package com.zero.android.network.model.serializer

import com.zero.android.models.enums.ProfileType
import com.zero.android.models.enums.toProfileType

object ProfileTypeSerializer : EnumSerializer<ProfileType>() {
	override fun String?.stringToEnum() = toProfileType()
}
