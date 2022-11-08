package com.zero.android.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.ui.components.form.CustomTextField
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme

@Composable
fun SearchView(
	modifier: Modifier = Modifier,
	searchText: String,
	placeHolder: String = stringResource(R.string.search),
	showSearchCancel: Boolean = true,
	onValueChanged: (String) -> Unit,
	onSearchCancelled: () -> Unit = {}
) {
	val focusRequester = remember { FocusRequester() }

	Row(modifier = modifier.padding(8.dp).fillMaxWidth()) {
		CustomTextField(
			value = searchText,
			onValueChange = { onValueChanged(it) },
			placeholder = placeHolder,
			shape = RoundedCornerShape(24.dp),
			modifier = Modifier.focusRequester(focusRequester).weight(1f),
			leadingIcon = {
				Icon(
					painterResource(R.drawable.ic_search),
					contentDescription = "",
					tint = AppTheme.colors.surface
				)
			},
			trailingIcon = {
				IconButton(onClick = { onValueChanged("") }, enabled = searchText.isNotEmpty()) {
					Icon(
						painter = painterResource(R.drawable.ic_cancel_24),
						contentDescription = "",
						tint = AppTheme.colors.surface
					)
				}
			}
		)
		if (showSearchCancel) {
			TextButton(
				onClick = {
					onValueChanged("")
					onSearchCancelled()
				}
			) {
				Text(stringResource(R.string.cancel), color = AppTheme.colors.colorTextPrimary)
			}
		}
	}

	LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Preview
@Composable
private fun SearchViewPreview() = Preview { SearchView(searchText = "Test", onValueChanged = {}) }
