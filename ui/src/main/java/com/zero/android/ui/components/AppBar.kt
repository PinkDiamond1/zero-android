package com.zero.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	modifier: Modifier = Modifier,
	scrollBehavior: TopAppBarScrollBehavior? = null,
	color: Color? = null,
	navIcon: @Composable () -> Unit,
	title: @Composable () -> Unit,
	actions: @Composable RowScope.() -> Unit = {}
) {
	Box(modifier = Modifier) {
		TopAppBar(
			modifier = modifier,
			actions = actions,
			title = title,
			scrollBehavior = scrollBehavior,
			navigationIcon = navIcon,
			colors =
			if (color != null) {
				TopAppBarDefaults.smallTopAppBarColors(containerColor = color)
			} else {
				TopAppBarDefaults.smallTopAppBarColors()
			}
		)
	}
}
