package com.zero.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zero.android.ui.extensions.Preview

@Composable
fun LoadingContainer(
	modifier: Modifier = Modifier,
	loading: Boolean,
	size: Dp = 42.dp,
	content: @Composable () -> Unit
) {
	if (loading) {
		Column(
			modifier = modifier.fillMaxSize().fillMaxWidth().defaultMinSize(minHeight = 100.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			CircularProgress(size = size)
		}
	} else {
		content()
	}
}

@Composable
fun OverlappingLoadingContainer(
	modifier: Modifier = Modifier,
	loading: Boolean,
	size: Dp = 42.dp,
	content: @Composable () -> Unit
) {
	Box(modifier, contentAlignment = Alignment.Center) {
		content()
		if (loading) {
			Column(
				modifier = modifier.fillMaxWidth().clickable {}.defaultMinSize(minHeight = 100.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				CircularProgress(size = size)
			}
		}
	}
}

@Preview
@Composable
fun LoadingContainerPreview() = Preview {
	LoadingContainer(loading = true) { Text(text = "Testing") }
}
