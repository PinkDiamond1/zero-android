package com.zero.android.common.util

object FileUtil {

	const val DIRECTORY_MEDIA = "/Media/"
	const val FILE_PROVIDER_AUTHORITY = "$APPLICATION_ID.provider"

	fun getExtension(url: String): String {
		return url.split('.').lastOrNull() ?: "jpg"
	}

	fun getFileName(url: String): String {
		return url.split('/').lastOrNull() ?: "File_${System.nanoTime()}.${getExtension(url)}"
	}
}
