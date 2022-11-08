package com.zero.android.common.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Context.getActivity(): Activity? =
	when (this) {
		is Activity -> this
		is ContextWrapper -> baseContext.getActivity()
		else -> null
	}

fun CoroutineScope.launchSafe(block: suspend () -> Unit) = launch { runCatching { block() } }

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
	Toast.makeText(this, message, duration).show()
}
