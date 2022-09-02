package com.zero.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zero.android.ui.theme.AppTheme

@Composable
fun StrikeLabel(
	text: String,
	textStyle: TextStyle = MaterialTheme.typography.labelSmall,
	textColor: Color = AppTheme.colors.colorTextSecondaryVariant,
	strikeColors: List<Color> = listOf(AppTheme.colors.surfaceInverse, AppTheme.colors.surface),
	paddingVertical: Dp = 8.dp,
	paddingHorizontal: Dp = 16.dp,
	strikeSize: Dp = 0.5.dp
) {
	Row(
		modifier =
		Modifier.padding(vertical = paddingVertical, horizontal = paddingHorizontal)
			.wrapContentHeight(),
		verticalAlignment = Alignment.CenterVertically
	) {
		PreStrikeLine(strikeColors, strikeSize)
		Text(
			text = text,
			modifier = Modifier.padding(horizontal = 16.dp),
			style = textStyle,
			color = textColor
		)
		PostStrikeLine(strikeColors, strikeSize)
	}
}

@Composable
private fun RowScope.PreStrikeLine(strikeColors: List<Color>, strikeSize: Dp) {
	Box(
		modifier =
		Modifier.weight(1f)
			.height(strikeSize)
			.background(brush = Brush.horizontalGradient(colors = strikeColors))
	)
}

@Composable
private fun RowScope.PostStrikeLine(strikeColors: List<Color>, strikeSize: Dp) {
	Box(
		modifier =
		Modifier.weight(1f)
			.height(strikeSize)
			.background(brush = Brush.horizontalGradient(colors = strikeColors.reversed()))
	)
}

@Preview @Composable
fun StrikeLabelPreview() {}
