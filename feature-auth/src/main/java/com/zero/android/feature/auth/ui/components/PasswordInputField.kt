package com.zero.android.feature.auth.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.zero.android.ui.theme.AppTheme

@Composable
fun PasswordTextField(
	modifier: Modifier = Modifier,
	placeHolder: @Composable () -> Unit,
	singleLine: Boolean = true,
	error: Int? = null,
	onTextChanged: (String) -> Unit = {},
	onFocusChanged: (Boolean) -> Unit = {},
	iconTint: Color = AppTheme.colors.glow,
	imeAction: ImeAction = ImeAction.Done
) {
	var passwordVisible by remember { mutableStateOf(false) }
	AuthInputField(
		modifier = modifier,
		placeHolder = placeHolder,
		singleLine = singleLine,
		error = error,
		onTextChanged = onTextChanged,
		onFocusChanged = onFocusChanged,
		visualTransformation =
		if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
		keyboardOptions =
		KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
		trailingIcon = {
			val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
			val description = if (passwordVisible) "Hide password" else "Show password"

			IconButton(onClick = { passwordVisible = !passwordVisible }) {
				Icon(imageVector = image, description, tint = iconTint)
			}
		}
	)
}
