package com.zero.android.data.repository

import com.zero.android.network.model.ApiFileInfo
import java.io.File

interface FileRepository {

	suspend fun upload(file: File): ApiFileInfo
}
