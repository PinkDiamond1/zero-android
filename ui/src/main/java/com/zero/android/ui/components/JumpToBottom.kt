package com.zero.android.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.ui.theme.AppTheme

private enum class Visibility {
	VISIBLE,
	GONE
}

@Composable
fun JumpToBottom(enabled: Boolean, onClicked: () -> Unit, modifier: Modifier = Modifier) {
	// Show Jump to Bottom button
	val transition =
		updateTransition(if (enabled) Visibility.VISIBLE else Visibility.GONE, label = "")
	val bottomOffset by
	transition.animateDp(label = "") {
		if (it == Visibility.GONE) {
			(-75).dp
		} else {
			75.dp
		}
	}
	if (bottomOffset > 0.dp) {
		ExtendedFloatingActionButton(
			icon = {
				Icon(
					imageVector = Icons.Filled.KeyboardArrowDown,
					modifier = Modifier.height(18.dp),
					contentDescription = null
				)
			},
			text = {
				Text(
					text = stringResource(R.string.latest_messages),
					style = MaterialTheme.typography.labelMedium
				)
			},
			onClick = onClicked,
			containerColor = AppTheme.colors.glow,
			contentColor = Color.White,
			modifier = modifier.offset(x = 0.dp, y = -bottomOffset).height(36.dp),
			shape = RoundedCornerShape(24.dp)
		)
	}
}

@Preview
@Composable
fun JumpToBottomPreview() {
	JumpToBottom(enabled = true, onClicked = {})
}
