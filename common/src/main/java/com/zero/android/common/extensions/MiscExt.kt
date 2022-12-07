package com.zero.android.common.extensions

import com.zero.android.common.system.Logger

suspend inline fun <T> nullable(logger: Logger? = null, crossinline block: suspend () -> T) =
	try {
		block()
	} catch (e: Exception) {
		logger?.e(e)
		null
	}

inline fun <T> nullableBlocking(logger: Logger? = null, crossinline block: () -> T) =
	try {
		block()
	} catch (e: Exception) {
		logger?.e(e)
		null
	}
