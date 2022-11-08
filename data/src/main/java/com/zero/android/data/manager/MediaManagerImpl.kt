package com.zero.android.data.manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.FileProvider
import com.zero.android.common.extensions.downloadFile
import com.zero.android.common.extensions.isValidUrl
import com.zero.android.common.extensions.toUrl
import com.zero.android.common.util.FileUtil
import com.zero.android.common.util.FileUtil.FILE_PROVIDER_AUTHORITY
import com.zero.android.models.ChatMedia
import com.zero.android.models.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

internal class MediaManagerImpl
@Inject
constructor(
	@ApplicationContext private val context: Context,
	private val fileManager: FileManager
) : MediaManager {

	private val cachePath by lazy { fileManager.getCachePath(FileUtil.DIRECTORY_MEDIA) }

	init {
		fileManager.generateDirs(cachePath)
	}

	override suspend fun download(media: ChatMedia) {
		media.mediaUrl?.let {
			val fileName = FileUtil.getFileName(it)
			if (it.isNotEmpty() && it.isValidUrl) {
				fileManager.downloadFile(fileName, it)
			}
		}
	}

	override suspend fun copyToClipboard(message: Message) {
		message.fileUrl?.let {
			val clipboardManager =
				context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
			val fileName = FileUtil.getFileName(it)
			val filePath = "$cachePath$fileName"
			val file = File(filePath)
			if (it.isNotEmpty() && it.isValidUrl) {
				it.toUrl.downloadFile(file.absolutePath)
				clipboardManager?.setPrimaryClip(
					ClipData.newUri(
						context.contentResolver,
						"",
						FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
					)
				)
			}
		}
	}
}
