package com.zero.android.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun ExtraSmallCircularImage(
	modifier: Modifier = Modifier,
	imageUrl: String? = null,
	@DrawableRes placeHolder: Int,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier.size(24.dp),
		imageUrl = imageUrl,
		placeHolder = placeHolder,
		contentDescription = contentDescription
	)
}

@Composable
fun SmallCircularImage(
	modifier: Modifier = Modifier,
	imageUrl: String? = null,
	@DrawableRes placeHolder: Int,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier.size(36.dp),
		imageUrl = imageUrl,
		placeHolder = placeHolder,
		contentDescription = contentDescription
	)
}

@Composable
fun MediumCircularImage(
	modifier: Modifier = Modifier,
	imageUrl: String? = null,
	@DrawableRes placeHolder: Int,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier.size(42.dp),
		imageUrl = imageUrl,
		placeHolder = placeHolder,
		contentDescription = contentDescription
	)
}

@Composable
fun BigCircularImage(
	modifier: Modifier = Modifier,
	imageUrl: String? = null,
	@DrawableRes placeHolder: Int,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier.size(54.dp),
		imageUrl = imageUrl,
		placeHolder = placeHolder,
		contentDescription = contentDescription
	)
}

@Composable
fun LargeCircularImage(
	modifier: Modifier = Modifier,
	imageUrl: String? = null,
	@DrawableRes placeHolder: Int,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier.size(64.dp),
		imageUrl = imageUrl,
		placeHolder = placeHolder,
		contentDescription = contentDescription
	)
}

@Composable
fun CircularImage(
	modifier: Modifier = Modifier,
	imageUrl: String? = null,
	@DrawableRes placeHolder: Int,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier,
		imageUrl = imageUrl,
		placeHolder = placeHolder,
		contentDescription = contentDescription
	)
}

@Composable
private fun NetworkImage(
	modifier: Modifier = Modifier,
	imageUrl: String? = null,
	@DrawableRes placeHolder: Int,
	contentDescription: String = ""
) {
	AsyncImage(
		model =
		ImageRequest.Builder(LocalContext.current)
			.memoryCachePolicy(CachePolicy.ENABLED)
			.diskCachePolicy(CachePolicy.ENABLED)
			.data(imageUrl)
			.crossfade(true)
			.crossfade(200)
			.build(),
		contentScale = ContentScale.Crop,
		placeholder = painterResource(placeHolder),
		error = painterResource(placeHolder),
		contentDescription = contentDescription,
		modifier = modifier.clip(CircleShape)
	)
}
