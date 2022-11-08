package com.zero.android.data.manager

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FileManagerImpl
@Inject
constructor(@ApplicationContext private val context: Context) : FileManager {

	override fun generateDirs(path: String) {
		File(path).apply { if (!this.exists()) this.mkdirs() }
	}

	override fun getCachePath(dir: String) = buildString {
		append(context.externalCacheDir?.absolutePath ?: "")
		append(dir)
	}

	override suspend fun downloadFile(fileName: String, fileUrl: String, directory: String) {
		val request =
			DownloadManager.Request(Uri.parse(fileUrl))
				.setTitle(fileName)
				.setDescription("Downloading...")
				.setDestinationInExternalPublicDir(directory, "/$fileName")
				.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
				.setAllowedOverMetered(true)
				.setAllowedNetworkTypes(
					DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
				)
		val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
		downloadManager.enqueue(request)
	}
}
