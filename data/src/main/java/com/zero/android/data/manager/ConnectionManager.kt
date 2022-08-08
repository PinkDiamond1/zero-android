package com.zero.android.data.manager

import android.content.Context

interface ConnectionManager {

	suspend fun connect()

	suspend fun disconnect(context: Context)
}
