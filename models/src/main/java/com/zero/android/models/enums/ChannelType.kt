package com.zero.android.models.enums

enum class ChannelType(val serializedName: String) {
	OPEN("open"),
	GROUP("group"),
	DIRECT_CHANNEL("direct_message_discussion"),
	DIRECT_CHANNEL_NAMED("named_direct_message_discussion")
}

fun String?.toChannelType() =
	when (this) {
		null -> ChannelType.OPEN
		else -> ChannelType.values().firstOrNull { type -> type.serializedName == this }
			?: ChannelType.OPEN
	}
