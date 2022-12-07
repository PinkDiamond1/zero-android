package com.zero.android.data.manager

import android.content.Context
import android.graphics.Bitmap
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import kotlinx.coroutines.flow.Flow

interface ImageLoader {

	fun preload(url: String)

	suspend fun load(url: String, size: Int? = null): Flow<Bitmap?>

	suspend fun load(url: String, width: Int? = null, height: Int? = null): Flow<Bitmap?>

	companion object {

		/** WorkAround for creating Global ImageLoader. Only use this function inside ZeroApp */
		fun getImageLoader(context: Context, diskCaching: Boolean = true): ImageLoader {
			var builder =
				ImageLoader.Builder(context).memoryCache {
					MemoryCache.Builder(context).maxSizePercent(0.25).build()
				}

			if (diskCaching) {
				builder =
					builder.diskCache {
						DiskCache.Builder()
							.directory(context.cacheDir.resolve("image_cache"))
							.maxSizePercent(0.04)
							.build()
					}
			}

			return builder.build()
		}
	}
}
