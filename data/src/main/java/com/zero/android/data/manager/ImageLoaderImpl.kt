package com.zero.android.data.manager

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ImageLoaderImpl
@Inject
constructor(@ApplicationContext private val context: Context) : ImageLoader {

	override fun preload(url: String) {
		context.imageLoader.enqueue(ImageRequest.Builder(context).data(url).build())
	}

	override suspend fun load(url: String, size: Int?) = load(url, size, size)

	override suspend fun load(url: String, width: Int?, height: Int?) = flow {
		emit(
			context.imageLoader
				.execute(
					ImageRequest.Builder(context)
						.apply {
							data(url)
							transformations(CircleCropTransformation())
							memoryCachePolicy(CachePolicy.ENABLED)
							allowConversionToBitmap(true)
							scale(Scale.FILL)
							if (width != null && height != null) {
								size(width = width, height = height)
							}
						}
						.build()
				)
				.drawable
				?.toBitmap()
		)
	}
}
