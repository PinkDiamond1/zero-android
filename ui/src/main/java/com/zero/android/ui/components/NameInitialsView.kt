package com.zero.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zero.android.common.extensions.initials
import com.zero.android.ui.theme.AppTheme

@Composable
fun NameInitialsCircle(modifier: Modifier, size: Dp = 36.dp, displayName: String) {
	Box(
		modifier =
		modifier
			.size(size)
			.background(color = AppTheme.colors.surfaceInverse, shape = CircleShape)
			.border(BorderStroke(1.dp, AppTheme.colors.colorTextSecondaryVariant), CircleShape)
	) {
		Text(
			displayName.initials(),
			modifier = Modifier.align(Alignment.Center),
			color = AppTheme.colors.surface,
			style = MaterialTheme.typography.displayLarge
		)
	}
}

@Composable
fun CircularInitialsImage(
	modifier: Modifier = Modifier,
	size: Dp = 36.dp,
	name: String,
	url: String?,
	placeholder: Painter? = null
) {
	if (url.isNullOrEmpty()) NameInitialsCircle(modifier = modifier, size = size, displayName = name)
	else CircularImage(modifier = modifier, size = size, url = url, placeholder = placeholder)
}
