package com.zero.android.database.converter

import androidx.room.TypeConverter
import com.zero.android.database.converter.AppJson.decodeJson
import com.zero.android.database.converter.AppJson.toJson
import com.zero.android.models.MessageMeta

class ObjectConverters {

	@TypeConverter
	fun stringToMessageMeta(value: String?): MessageMeta? = value?.decodeJson<MessageMeta>()

	@TypeConverter fun string(messageMeta: MessageMeta?): String? = messageMeta?.toJson()
}
