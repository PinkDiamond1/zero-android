package com.zero.android.data.extensions

import com.zero.android.common.util.NotFoundException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun CoroutineScope.launchSafeApi(block: suspend () -> Unit) = launch {
	try {
		block()
	} catch (e: NotFoundException) {
		throw e
	} catch (_: Exception) {}
}
