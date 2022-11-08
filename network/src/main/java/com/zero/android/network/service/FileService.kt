package com.zero.android.network.service

import com.zero.android.network.model.ApiFileInfo
import com.zero.android.network.model.ApiUploadInfo
import okhttp3.MultipartBody
import retrofit2.http.*

interface FileService {

	@GET("upload/info")
	suspend fun getUploadInfo(): ApiUploadInfo

	@Multipart @POST
	suspend fun upload(@Url url: String, @Part part: MultipartBody.Part): ApiFileInfo
}
