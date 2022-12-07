package com.zero.android.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
	modifier: Modifier = Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	color: Color = AppTheme.colors.colorTextPrimary,
	bgcolor: Color = AppTheme.colors.surfaceVariant,
	textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color),
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions(),
	singleLine: Boolean = true,
	maxLines: Int = Int.MAX_VALUE,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	placeholder: String = "",
	onTextLayout: (TextLayoutResult) -> Unit = {},
	shape: Shape = RectangleShape,
	isError: Boolean = false,
	padding: PaddingValues = PaddingValues(0.dp)
) {
	BasicTextField(
		modifier = modifier.fillMaxWidth().background(bgcolor, shape),
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
		cursorBrush = SolidColor(AppTheme.colors.colorTextPrimary),
		decorationBox = { innerTextField ->
			TextFieldDefaults.TextFieldDecorationBox(
				value = value,
				visualTransformation = visualTransformation,
				innerTextField = innerTextField,
				placeholder = placeholder.let { { Text(it, style = textStyle) } },
				label = null,
				leadingIcon = leadingIcon,
				trailingIcon = trailingIcon,
				singleLine = singleLine,
				enabled = enabled,
				isError = isError,
				interactionSource = interactionSource,
				colors = TextFieldDefaults.textFieldColors(textColor = color),
				contentPadding = padding
			)
		}
	)
}

@Preview
@Composable
private fun CustomTextFieldPreview() = Preview {
	CustomTextField(
		value = "Test",
		leadingIcon = {
			Icon(
				painterResource(R.drawable.ic_search),
				contentDescription = "",
				tint = AppTheme.colors.surface
			)
		},
		trailingIcon = {
			Icon(
				painter = painterResource(R.drawable.ic_cancel_24),
				contentDescription = "",
				tint = AppTheme.colors.surface
			)
		},
		padding = PaddingValues(16.dp),
		onValueChange = {}
	)
}
