package com.zero.android.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularImage(
	modifier: Modifier = Modifier,
	size: Dp = 36.dp,
	url: String? = null,
	placeholder: Painter? = null,
	contentDescription: String = ""
) {
	NetworkImage(
		modifier = modifier.size(size).clip(CircleShape),
		url = url,
		placeholder = placeholder ?: ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
		contentDescription = contentDescription
	)
}
