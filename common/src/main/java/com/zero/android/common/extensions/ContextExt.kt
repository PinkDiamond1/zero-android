package com.zero.android.common.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast

val Context.notificationManager
	get() = (getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager?)

fun Context.getActivity(): Activity? =
	when (this) {
		is Activity -> this
		is ContextWrapper -> baseContext.getActivity()
		else -> null
	}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
	Toast.makeText(this, message, duration).show()
}
