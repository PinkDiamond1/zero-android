package com.zero.android.database.converter

import androidx.room.TypeConverter
import com.zero.android.database.converter.AppJson.decodeJson
import com.zero.android.database.converter.AppJson.toJson
import com.zero.android.models.Education
import com.zero.android.models.Experience
import com.zero.android.models.FileThumbnail
import com.zero.android.models.Investment
import com.zero.android.models.MessageReaction
import com.zero.android.models.Valuable

class ListConverters {

	@TypeConverter
	fun stringToValuableList(value: String?): List<Valuable>? = value?.decodeJson<List<Valuable>>()

	@TypeConverter fun valuableListToString(value: List<Valuable>?) = value?.toJson()

	@TypeConverter
	fun stringToEducationList(value: String?): List<Education>? = value?.decodeJson<List<Education>>()

	@TypeConverter fun educationListToString(value: List<Education>?) = value?.toJson()

	@TypeConverter
	fun stringToInvestmentList(value: String?): List<Investment>? =
		value?.decodeJson<List<Investment>>()

	@TypeConverter fun investmentListToString(value: List<Investment>?) = value?.toJson()

	@TypeConverter
	fun stringToExperienceList(value: String?): List<Experience>? =
		value?.decodeJson<List<Experience>>()

	@TypeConverter fun experienceListToString(value: List<Experience>?) = value?.toJson()

	@TypeConverter
	fun stringToStringList(value: String?): List<String>? = value?.decodeJson<List<String>>()

	@TypeConverter fun stringListToString(value: List<String>?): String? = value?.toJson()

	@TypeConverter
	fun stringToStringMap(value: String?): Map<String, String?>? =
		value?.decodeJson<Map<String, String?>>()

	@TypeConverter fun stringMapToString(value: Map<String, String?>?) = value?.toJson()

	@TypeConverter
	fun stringToMessageReactionList(value: String?): List<MessageReaction>? =
		value?.decodeJson<List<MessageReaction>>()

	@TypeConverter fun messageReactionListToString(value: List<MessageReaction>?) = value?.toJson()

	@TypeConverter
	fun stringToFileThumbnailList(value: String?): List<FileThumbnail>? =
		value?.decodeJson<List<FileThumbnail>>()

	@TypeConverter fun fileThumbnailListToString(value: List<FileThumbnail>?) = value?.toJson()
}
