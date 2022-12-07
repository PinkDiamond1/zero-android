package com.zero.android.database.converter

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AppJson {
	val AppJson = Json { ignoreUnknownKeys = true }

	inline fun <reified T> String.decodeJson() =
		takeIf { this.isNotEmpty() }?.let { AppJson.decodeFromString<T>(it) }

	inline fun <reified T> T.toJson() = AppJson.encodeToString(this)
}
