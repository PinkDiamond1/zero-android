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
import androidx.compose.ui.unit.dp
import com.zero.android.common.extensions.initials
import com.zero.android.ui.theme.AppTheme

private val DEFAULT_MODIFIER = Modifier.size(36.dp)

@Composable
fun NameInitialsView(modifier: Modifier = DEFAULT_MODIFIER, userName: String) {
	Box(
		modifier =
		modifier
			.background(color = AppTheme.colors.surfaceInverse, shape = CircleShape)
			.border(BorderStroke(1.dp, AppTheme.colors.colorTextSecondaryVariant), CircleShape)
	) {
		Text(
			userName.initials(),
			modifier = Modifier.align(Alignment.Center),
			color = AppTheme.colors.surface,
			style = MaterialTheme.typography.displayLarge
		)
	}
}
