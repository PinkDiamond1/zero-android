package com.zero.android.models.enums

enum class MessageType(val serializedName: String) {
	TEXT("text"),
	IMAGE("image"),
	AUDIO("audio"),
	VIDEO("video"),
	UNKNOWN("");

	companion object {
		val mediaMessageTypes
			get() = values().filterNot { it == TEXT }
	}
}

fun String?.toMessageType() =
	when (this) {
		null -> MessageType.UNKNOWN
		else -> MessageType.values().firstOrNull { type -> type.serializedName == this }
			?: MessageType.UNKNOWN
	}
