package com.zero.android.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountBadge(
	modifier: Modifier = Modifier,
	style: TextStyle = MaterialTheme.typography.labelLarge,
	count: Int
) {
	Badge(
		modifier = modifier.padding(horizontal = 4.dp),
		containerColor = AppTheme.colors.glow,
		contentColor = Color.Black
	) {
		Text(text = count.toString(), style = style, color = Color.Black)
	}
}

@Preview @Composable
fun CountBadgePreview() = Preview { CountBadge(count = 1) }
