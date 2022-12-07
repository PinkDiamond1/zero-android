package com.zero.android.models.enums

enum class ProfileType(val serializedName: String) {
	ZERO("zero"),
	TELEGRAM("telegram")
}

fun String?.toProfileType() =
	when (this) {
		null -> ProfileType.ZERO
		else -> ProfileType.values().firstOrNull { type -> type.serializedName == this }
			?: ProfileType.ZERO
	}
