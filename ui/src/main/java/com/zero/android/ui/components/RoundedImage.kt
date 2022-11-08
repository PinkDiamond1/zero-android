package com.zero.android.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedImage(
	modifier: Modifier = Modifier,
	size: Dp = 36.dp,
	radius: Int = 8,
	url: String? = null,
	placeHolder: Painter,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier.size(size).clip(RoundedCornerShape(CornerSize(radius))),
		url = url,
		placeholder = placeHolder,
		contentDescription = contentDescription
	)
}
