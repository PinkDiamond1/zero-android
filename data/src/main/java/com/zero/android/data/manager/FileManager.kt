package com.zero.android.data.manager

import android.os.Environment

interface FileManager {

	fun generateDirs(path: String)

	fun getCachePath(dir: String): String

	suspend fun downloadFile(
		fileName: String,
		fileUrl: String,
		directory: String = Environment.DIRECTORY_DOWNLOADS
	)
}
