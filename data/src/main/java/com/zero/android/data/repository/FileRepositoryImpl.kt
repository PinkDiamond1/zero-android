package com.zero.android.data.repository

import com.zero.android.common.system.Logger
import com.zero.android.network.model.ApiFileInfo
import com.zero.android.network.service.FileService
import com.zero.android.network.util.NetworkFileUtil
import java.io.File
import javax.inject.Inject

internal class FileRepositoryImpl
@Inject
constructor(
	private val fileService: FileService,
	private val networkFileUtil: NetworkFileUtil,
	private val logger: Logger
) : FileRepository {

	override suspend fun upload(file: File): ApiFileInfo {
		val uploadInfo = fileService.getUploadInfo()
		return if (uploadInfo.apiUrl.isNotEmpty() && uploadInfo.query != null) {
			fileService.upload(
				networkFileUtil.getUploadUrl(uploadInfo),
				networkFileUtil.getUploadBody(file)
			)
		} else {
			logger.w("Upload Info is required for file upload")
			throw IllegalStateException("Upload Info is required for file upload")
		}
	}
}
