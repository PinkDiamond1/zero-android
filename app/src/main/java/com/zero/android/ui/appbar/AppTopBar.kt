package com.zero.android.ui.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R.drawable
import com.zero.android.models.Network
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.customTextStyle
import com.zero.android.ui.util.Preview

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
				when {
					!network?.displayName.isNullOrEmpty() || !network?.logo.isNullOrEmpty() ->
						CircularInitialsImage(
							size = 36.dp,
							name = network?.displayName!!,
							url = network.logo
						)
					else ->
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
