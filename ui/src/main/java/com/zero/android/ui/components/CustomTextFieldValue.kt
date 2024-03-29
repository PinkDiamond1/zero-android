package com.zero.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.zero.android.ui.theme.AppTheme

@Composable
fun CustomTextFieldValue(
	value: TextFieldValue,
	onValueChange: (TextFieldValue) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions(),
	singleLine: Boolean = false,
	maxLines: Int = Int.MAX_VALUE,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	placeholderText: String = "",
	placeHolderTextStyle: TextStyle = LocalTextStyle.current,
	onTextLayout: (TextLayoutResult) -> Unit = {},
	cursorBrush: Brush = SolidColor(AppTheme.colors.colorTextPrimary),
	shape: Shape = RectangleShape
) {
	BasicTextField(
		modifier = modifier.fillMaxWidth().background(AppTheme.colors.surfaceVariant, shape),
		value = value,
		onValueChange = onValueChange,
		singleLine = singleLine,
		maxLines = maxLines,
		enabled = enabled,
		readOnly = readOnly,
		interactionSource = interactionSource,
		textStyle = textStyle,
		visualTransformation = visualTransformation,
		keyboardOptions = keyboardOptions,
		keyboardActions = keyboardActions,
		onTextLayout = onTextLayout,
		cursorBrush = cursorBrush,
		decorationBox = { innerTextField ->
			Row(modifier, verticalAlignment = Alignment.CenterVertically) {
				if (leadingIcon != null) {
					Spacer(modifier = Modifier.size(12.dp))
					leadingIcon()
					Spacer(modifier = Modifier.size(12.dp))
				}
				Box(Modifier.weight(1f).padding(horizontal = 10.dp)) {
					if (value.text.isEmpty()) Text(placeholderText, style = placeHolderTextStyle)
					innerTextField()
				}
				if (trailingIcon != null) trailingIcon()
			}
		}
	)
}
