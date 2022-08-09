package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.ui.components.CustomTextFieldValue
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import kotlinx.coroutines.flow.collectLatest

private enum class InputSelector {
	TEXT,
	ATTACHMENT,
	IMAGE,
	VOICE_MEMO
}

@Preview
@Composable
fun UserInputPreview() {
	UserInputPanel(onMessageSent = {})
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun UserInputPanel(
	modifier: Modifier = Modifier,
	initialText: String = "",
	onMessageSent: (String) -> Unit,
	resetScroll: () -> Unit = {},
	addAttachment: () -> Unit = {},
	addImage: () -> Unit = {},
	recordMemo: () -> Unit = {},
    onTextChanged: (String) -> Unit = {},
) {
	val keyboardController = LocalSoftwareKeyboardController.current
	var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.TEXT) }
	val dismissKeyboard = { currentInputSelector = InputSelector.TEXT }

	// Intercept back navigation if there's a InputSelector visible
	if (currentInputSelector != InputSelector.TEXT) {
		BackHandler(onBack = dismissKeyboard)
	}
	val updatedMessage = prepareInitialMessage(initialText)
	var textState by remember {
		mutableStateOf(TextFieldValue(updatedMessage, TextRange(updatedMessage.length)))
	}
	LaunchedEffect(Unit) {
		MessageActionStateHandler.messageUpdatedText.collectLatest {
			if (it.isNotEmpty()) {
				textState = TextFieldValue(it, TextRange(it.length))
				MessageActionStateHandler.messageUpdatedText.emit("")
			}
		}
	}

	// Used to decide if the keyboard should be shown
	var textFieldFocusState by remember { mutableStateOf(false) }

	Row(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
		IconButton(
			modifier = Modifier.align(CenterVertically),
			onClick = {
				currentInputSelector = InputSelector.ATTACHMENT
				addAttachment()
			}
		) { Icon(imageVector = Icons.Filled.Add, contentDescription = "cd_add_attachment") }
		UserInputText(
			modifier = Modifier.fillMaxWidth().weight(1f).align(CenterVertically),
			textFieldValue = textState,
			onTextChanged = {
                textState = it
                onTextChanged(it.text)
            },
			// Only show the keyboard if there's no input selector and text field has focus
			keyboardShown = currentInputSelector == InputSelector.TEXT && textFieldFocusState,
			// Close extended selector if text field receives focus
			onTextFieldFocused = { focused ->
				if (focused) {
					currentInputSelector = InputSelector.TEXT
					resetScroll()
				}
				textFieldFocusState = focused
			},
			onMessageSent = {
				if (it.isNotEmpty()) {
					onMessageSent(it)
					textState = TextFieldValue()
					keyboardController?.hide()
				}
			}
		)
		IconButton(
			modifier = Modifier.align(CenterVertically),
			onClick = {
				currentInputSelector = InputSelector.IMAGE
				addImage()
			}
		) {
			Icon(
				painter = painterResource(R.drawable.ic_camera),
				contentDescription = "cd_add_attachment"
			)
		}
		IconButton(
			modifier = Modifier.align(CenterVertically),
			onClick = {
				currentInputSelector = InputSelector.VOICE_MEMO
				recordMemo()
			}
		) {
			Icon(painter = painterResource(R.drawable.ic_mic), contentDescription = "cd_record_audio")
		}
	}
}

private fun prepareInitialMessage(initialMessage: String): String {
	val regex = Regex("@\\[(.*?)\\]\\(user\\:[-_a-z0-9]+\\)")
	val matches = regex.findAll(initialMessage).map { it.value }
	var updatedMessage = initialMessage
	matches.distinct().forEach {
		val userName = it.substringBefore("]").replace("[", "")
		updatedMessage = updatedMessage.replace(it, userName)
	}
	return updatedMessage
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@ExperimentalFoundationApi
@Composable
private fun UserInputText(
	modifier: Modifier = Modifier,
	keyboardType: KeyboardType = KeyboardType.Text,
	textFieldValue: TextFieldValue,
	keyboardShown: Boolean,
	onMessageSent: (String) -> Unit,
	onTextChanged: (TextFieldValue) -> Unit,
	onTextFieldFocused: (Boolean) -> Unit
) {
	Box(modifier = modifier.semantics { keyboardShownProperty = keyboardShown }) {
		var lastFocusState by remember { mutableStateOf(false) }
		CustomTextFieldValue(
			value = textFieldValue,
			onValueChange = {
				onTextChanged(it)
				MessageActionStateHandler.onMessageTextChanged(it.text)
			},
			placeholderText = stringResource(R.string.write_your_message),
			textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
			placeHolderTextStyle =
			MaterialTheme.typography.bodyMedium.copy(color = AppTheme.colors.colorTextSecondary),
			modifier =
			Modifier.fillMaxWidth().padding(12.dp).align(Alignment.CenterStart).onFocusChanged {
					state ->
				if (lastFocusState != state.isFocused) {
					onTextFieldFocused(state.isFocused)
				}
				lastFocusState = state.isFocused
			},
			shape = RoundedCornerShape(24.dp),
			keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Send),
			keyboardActions = KeyboardActions(onSend = { onMessageSent(textFieldValue.text) })
		)
	}
}
