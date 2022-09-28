package com.zero.android.data.manager

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

interface ImageLoader {

	fun preload(url: String)

	companion object {

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
