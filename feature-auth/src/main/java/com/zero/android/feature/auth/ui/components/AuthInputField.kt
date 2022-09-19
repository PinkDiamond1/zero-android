package com.zero.android.feature.auth.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.zero.android.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthInputField(
	modifier: Modifier = Modifier,
	placeHolder: @Composable () -> Unit,
	trailingIcon: @Composable () -> Unit = {},
	singleLine: Boolean = true,
	focusedByDefault: Boolean = false,
	error: Int? = null,
	onTextChanged: (String) -> Unit = {},
	onFocusChanged: (Boolean) -> Unit = {},
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
	val text = remember { mutableStateOf("") }
	val focusRequester = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current
	val isError = error != null
	val errorIcon: @Composable () -> Unit = {
		Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
	}

	Column(modifier = Modifier.fillMaxWidth()) {
		OutlinedTextField(
			modifier =
			modifier.focusRequester(focusRequester).onFocusChanged { state ->
				onFocusChanged(state.isFocused)
			},
			value = text.value,
			onValueChange = {
				text.value = it
				onTextChanged(it)
			},
			label = placeHolder,
			trailingIcon = if (isError) errorIcon else trailingIcon,
			singleLine = singleLine,
			shape = RoundedCornerShape(35.dp),
			visualTransformation = visualTransformation,
			keyboardOptions = keyboardOptions,
			keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
			colors =
			TextFieldDefaults.outlinedTextFieldColors(
				textColor = Color.White,
				focusedBorderColor = AppTheme.colors.glow,
				unfocusedBorderColor = AppTheme.colors.glow.copy(0.25f),
				focusedLabelColor = AppTheme.colors.glow,
				unfocusedLabelColor = Color.White.copy(0.25f)
			),
			isError = isError
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
