package com.zero.android.ui.components.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.ui.extensions.Preview

@Composable
fun TextField(
	modifier: Modifier = Modifier,
	value: String,
	label: String? = null,
	trailingIcon: @Composable () -> Unit = {},
	singleLine: Boolean = true,
	focusedByDefault: Boolean = false,
	error: Int? = null,
	onTextChanged: (String) -> Unit = {},
	onFocusChanged: (Boolean) -> Unit = {},
	onKeyboardAction: (() -> Unit)? = null,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
	val focusRequester = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current
	val isError = error != null

	val errorIcon: @Composable () -> Unit = {
		Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
	}

	Column(modifier = modifier.fillMaxWidth()) {
		label?.let {
			Text(
				text = label,
				color = MaterialTheme.colorScheme.surfaceVariant,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(bottom = 8.dp)
			)
		}
		CustomTextField(
			modifier =
			Modifier.focusRequester(focusRequester).onFocusChanged { state ->
				onFocusChanged(state.isFocused)
			},
			value = value,
			padding = PaddingValues(16.dp),
			onValueChange = { onTextChanged(it) },
			shape = RoundedCornerShape(8.dp),
			trailingIcon = if (isError) errorIcon else trailingIcon,
			singleLine = singleLine,
			visualTransformation = visualTransformation,
			keyboardOptions = keyboardOptions,
			keyboardActions =
			KeyboardActions(
				onDone = {
					focusManager.clearFocus()
					onKeyboardAction?.invoke()
				}
			)
		)
		if (isError) {
			Text(
				text = error?.let { stringResource(it) } ?: "",
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.padding(horizontal = 40.dp, vertical = 4.dp)
			)
		}
	}

	LaunchedEffect(Unit) {
		if (focusedByDefault) {
			focusRequester.requestFocus()
		}
	}
}

@Preview
@Composable
private fun TextFieldPreview() = Preview { TextField(value = "Test", label = "Label") }
