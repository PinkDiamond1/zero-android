package com.zero.android.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
internal fun NetworkImage(
	modifier: Modifier = Modifier,
	url: String? = null,
	placeholder: Painter,
	contentDescription: String = ""
) {
	AsyncImage(
		model =
		ImageRequest.Builder(LocalContext.current)
			.memoryCachePolicy(CachePolicy.ENABLED)
			.diskCachePolicy(CachePolicy.ENABLED)
			.data(url)
			.crossfade(true)
			.crossfade(400)
			.build(),
		contentScale = ContentScale.Crop,
		error = placeholder,
		contentDescription = contentDescription,
		modifier = modifier
	)
}
