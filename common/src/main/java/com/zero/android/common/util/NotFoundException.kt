package com.zero.android.common.util

class NotFoundException(override val cause: Throwable?, override val message: String? = null) :
	Exception()
