package com.zero.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zero.android.common.extensions.initials

private val DEFAULT_MODIFIER = Modifier.size(36.dp)

@Composable
fun NameInitialsView(modifier: Modifier = DEFAULT_MODIFIER, userName: String) {
	Box(
		modifier =
		modifier.background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
	) {
		Text(
			userName.initials(),
			modifier = Modifier.align(Alignment.Center),
			color = Color.White,
			style = MaterialTheme.typography.displayLarge
		)
	}
}
