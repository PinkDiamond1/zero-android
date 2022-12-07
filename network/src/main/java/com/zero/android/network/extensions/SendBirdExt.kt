package com.zero.android.network.extensions

import com.sendbird.android.SendBirdException
import com.zero.android.common.util.NotFoundException

internal val SendBirdException.parsed: Throwable
	get() = run {
		if (message?.contains("must be a member") == true || message?.contains("not found") == true) {
			NotFoundException(cause ?: this, message)
		} else {
			cause ?: this
		}
	}
