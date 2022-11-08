package com.zero.android.network.extensions

import com.sendbird.android.SendBirdException

internal val SendBirdException.parsed
	get() = cause ?: this
