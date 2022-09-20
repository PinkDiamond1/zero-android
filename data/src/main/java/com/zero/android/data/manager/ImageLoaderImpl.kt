package com.zero.android.data.manager

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ImageLoaderImpl
@Inject
constructor(@ApplicationContext private val context: Context) : ImageLoader {

	override fun preload(url: String) {
		context.imageLoader.enqueue(ImageRequest.Builder(context).data(url).build())
	}
}
