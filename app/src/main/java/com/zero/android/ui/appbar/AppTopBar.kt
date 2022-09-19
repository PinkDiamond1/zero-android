package com.zero.android.ui.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.zero.android.common.R.drawable
import com.zero.android.models.Network
import com.zero.android.ui.components.SmallCircularImage
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.customTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
	modifier: Modifier = Modifier,
	network: Network?,
	openDrawer: () -> Unit,
	actions: @Composable RowScope.() -> Unit = {}
) {
	CenterAlignedTopAppBar(
		modifier = modifier,
		title = {
			Text(
				network?.displayName ?: "",
				style = MaterialTheme.typography.customTextStyle(LocalTextStyle.current)
			)
		},
		navigationIcon = {
			IconButton(onClick = openDrawer) {
				if (!network?.logo.isNullOrEmpty()) {
					SmallCircularImage(
						imageUrl = network?.logo,
						placeHolder = drawable.ic_circular_image_placeholder
					)
				} else {
					Icon(
						painter = painterResource(id = drawable.ic_menu),
						contentDescription = "Menu Icon",
						tint = AppTheme.colors.surface
					)
				}
			}
		},
		actions = actions
	)
}

@Preview @Composable
fun AppTopBarPreview() = Preview {}
