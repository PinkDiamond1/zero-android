package com.zero.android.common.util

import com.zero.android.common.extensions.plural
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

object DateUtil {

	fun getTimeAgoString(milliseconds: Long): String {
		val time = Instant.fromEpochMilliseconds(milliseconds)
		val period = time.periodUntil(Clock.System.now(), TimeZone.UTC)
		return if (period.years != 0) {
			"${period.years} ${"year".plural(period.years)} ago"
		} else if (period.months != 0) {
			"${period.months} ${"month".plural(period.months)} ago"
		} else if (period.days != 0) {
			"${period.days} ${"day".plural(period.days)} ago"
		} else if (period.hours != 0) {
			"${period.hours} ${"hour".plural(period.hours)} ago"
		} else if (period.minutes != 0) {
			"${period.minutes} ${"min".plural(period.minutes)} ago"
		} else if (period.seconds != 0) {
			"few seconds ago"
		} else {
			"moments ago"
		}
	}

	fun currentTimeMillis() = Clock.System.now().toEpochMilliseconds()
}
