package com.zero.android.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zero.android.ui.theme.AppTheme

@Composable
fun ClickableIcon(
	modifier: Modifier = Modifier,
	@DrawableRes icon: Int,
	onClick: () -> Unit,
	size: Dp = 24.dp,
	contentDescription: String? = null,
	tint: Color = AppTheme.colors.surface,
	includePadding: Boolean = true
) {
	Row(modifier) {
		IconButton(onClick = onClick, modifier = Modifier.size(size)) {
			Icon(painter = painterResource(icon), contentDescription = contentDescription, tint = tint)
		}
		if (includePadding) {
			Spacer(modifier = Modifier.size(10.dp))
		}
	}
}

@Composable
fun SmallClickableIcon(
	modifier: Modifier = Modifier,
	@DrawableRes icon: Int,
	onClick: () -> Unit,
	contentDescription: String? = null,
	tint: Color = AppTheme.colors.surface,
	includePadding: Boolean = true
) {
	Row(modifier) {
		IconButton(onClick = onClick, modifier = Modifier.size(26.dp)) {
			Icon(painter = painterResource(icon), contentDescription = contentDescription, tint = tint)
		}
		if (includePadding) {
			Spacer(modifier = Modifier.size(10.dp))
		}
	}
}

@Composable
fun SmallClickableIcon(
	modifier: Modifier = Modifier,
	vector: ImageVector,
	onClick: () -> Unit,
	contentDescription: String? = null,
	tint: Color = AppTheme.colors.surface,
	includePadding: Boolean = true
) {
	Row(modifier) {
		IconButton(onClick = onClick, modifier = Modifier.size(26.dp)) {
			Icon(imageVector = vector, contentDescription = contentDescription, tint = tint)
		}
		if (includePadding) {
			Spacer(modifier = Modifier.size(10.dp))
		}
	}
}

@Composable
fun MediumClickableIcon(
	modifier: Modifier = Modifier,
	@DrawableRes icon: Int,
	onClick: () -> Unit,
	contentDescription: String? = null,
	tint: Color = AppTheme.colors.surface,
	includePadding: Boolean = true
) {
	Row(modifier) {
		IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
			Icon(painter = painterResource(icon), contentDescription = contentDescription, tint = tint)
		}
		if (includePadding) {
			Spacer(modifier = Modifier.size(10.dp))
		}
	}
}

@Composable
fun LargeClickableIcon(
	modifier: Modifier = Modifier,
	@DrawableRes icon: Int,
	onClick: () -> Unit,
	contentDescription: String? = null,
	tint: Color = AppTheme.colors.surface,
	includePadding: Boolean = true
) {
	Row(modifier) {
		IconButton(onClick = onClick, modifier = Modifier.size(38.dp)) {
			Icon(painter = painterResource(icon), contentDescription = contentDescription, tint = tint)
		}
		if (includePadding) {
			Spacer(modifier = Modifier.size(10.dp))
		}
	}
}
