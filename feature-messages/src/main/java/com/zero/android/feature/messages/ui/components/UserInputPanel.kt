package com.zero.android.feature.messages.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.zero.android.common.R
import com.zero.android.common.extensions.getActivity
import com.zero.android.common.extensions.isVideoFile
import com.zero.android.common.extensions.toFile
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.CustomTextFieldValue
import com.zero.android.ui.components.SmallClickableIcon
import com.zero.android.ui.manager.GalleryManager
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import kotlinx.coroutines.flow.collectLatest
import java.io.File

private enum class InputSelector {
	TEXT,
	ATTACHMENT,
	IMAGE,
	VOICE_MEMO
}

@Preview @Composable
fun UserInputPreview() {}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun UserInputPanel(
	modifier: Modifier = Modifier,
	initialText: String = "",
	focused: Boolean = false,
	onMessageSent: (String) -> Unit,
	onMediaSelected: (File, MessageType) -> Unit,
	resetScroll: () -> Unit = {},
	recordMemo: () -> Unit = {},
	onTextChanged: (String) -> Unit = {}
) {
	val context = LocalContext.current
	val keyboardController = LocalSoftwareKeyboardController.current
	val focusRequester = remember { FocusRequester() }
	var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.TEXT) }
	val dismissKeyboard = { currentInputSelector = InputSelector.TEXT }

	val imageSelectorLauncher =
		rememberLauncherForActivityResult(
			contract = ActivityResultContracts.StartActivityForResult(),
			onResult = {
				onMediaResult(it, context, onMediaSelected)
				resetScroll()
			}
		)

	// Intercept back navigation if there's a InputSelector visible
	if (currentInputSelector != InputSelector.TEXT) {
		BackHandler(onBack = dismissKeyboard)
	}
	val updatedMessage = prepareInitialMessage(initialText)
	var textState by
	remember(initialText) {
		mutableStateOf(TextFieldValue(updatedMessage, TextRange(updatedMessage.length)))
	}
	if (focused) {
		focusRequester.requestFocus()
		keyboardController?.show()
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
	var textFieldFocusState by remember { mutableStateOf(focused) }
	var showSendButton by remember { mutableStateOf(false) }

	Row(modifier = modifier.fillMaxWidth(), verticalAlignment = CenterVertically) {
		Spacer(modifier = Modifier.size(10.dp))
		SmallClickableIcon(
			vector = Icons.Filled.Add,
			contentDescription = "Add",
			onClick = {
				currentInputSelector = InputSelector.ATTACHMENT
				selectChatMedia(context, false) { imageSelectorLauncher.launch(it) }
			}
		)
		UserInputText(
			modifier = Modifier.weight(1f),
			textFieldValue = textState,
			onTextChanged = {
				textState = it
				onTextChanged(it.text)
				showSendButton = it.text.isNotEmpty()
			},
			// Only show the keyboard if there's no input selector and text field has focus
			keyboardShown = currentInputSelector == InputSelector.TEXT && textFieldFocusState,
			focusRequester = focusRequester,
			// Close extended selector if text field receives focus
			onTextFieldFocused = { focused ->
				if (focused) {
					currentInputSelector = InputSelector.TEXT
					resetScroll()
				}
				textFieldFocusState = focused
				if (!focused) {
					keyboardController?.hide()
				}
			},
			onMessageSent = {
				if (it.isNotEmpty()) {
					onMessageSent(it)
					resetScroll()
					textState = TextFieldValue()
					showSendButton = false
				}
			}
		)
		Spacer(modifier = Modifier.size(10.dp))
		if (showSendButton) {
			SmallClickableIcon(
				icon = R.drawable.ic_send_24,
				contentDescription = "Send",
				onClick = {
					if (textState.text.isNotEmpty()) {
						onMessageSent(textState.text)
						resetScroll()
						textState = TextFieldValue()
						showSendButton = false
					}
				},
				tint = AppTheme.colors.glow
			)
		} else {
			SmallClickableIcon(
				icon = R.drawable.ic_camera,
				contentDescription = "Camera",
				onClick = {
					currentInputSelector = InputSelector.IMAGE
					selectChatMedia(context, true) { imageSelectorLauncher.launch(it) }
				}
			)
			SmallClickableIcon(
				modifier =
				Modifier.pointerInteropFilter {
					when (it.action) {
						MotionEvent.ACTION_DOWN -> {
							currentInputSelector = InputSelector.VOICE_MEMO
							recordMemo()
						}
						else -> false
					}
					true
				},
				icon = R.drawable.ic_mic,
				contentDescription = "Audio",
				onClick = {}
			)
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

private fun selectChatMedia(
	context: Context,
	fromCamera: Boolean,
	onImagePicker: (Intent) -> Unit
) {
	context.getActivity()?.let { GalleryManager.getChatMediaPicker(it, fromCamera, onImagePicker) }
}

fun onMediaResult(
	activityResult: ActivityResult,
	context: Context,
	onMediaSelected: (File, MessageType) -> Unit
) {
	val resultCode = activityResult.resultCode
	val data = activityResult.data
	if (resultCode == Activity.RESULT_OK) {
		// Image Uri will not be null for RESULT_OK
		val fileUri = data?.data!!
		val file =
			try {
				fileUri.toFile()
			} catch (e: Exception) {
				fileUri.toFile(context)
			}
		val type = if (fileUri.isVideoFile(context)) MessageType.VIDEO else MessageType.IMAGE
		onMediaSelected(file, type)
	}
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
	focusRequester: FocusRequester,
	onMessageSent: (String) -> Unit,
	onTextChanged: (TextFieldValue) -> Unit,
	onTextFieldFocused: (Boolean) -> Unit
) {
	Box(modifier = modifier.semantics { keyboardShownProperty = keyboardShown }) {
		CustomTextFieldValue(
			value = textFieldValue,
			onValueChange = {
				onTextChanged(it)
				MessageActionStateHandler.onMessageTextChanged(it.text)
			},
			placeholderText = stringResource(R.string.write_your_message),
			textStyle =
			MaterialTheme.typography.bodyMedium.copy(color = AppTheme.colors.colorTextPrimary),
			placeHolderTextStyle =
			MaterialTheme.typography.bodyMedium.copy(color = AppTheme.colors.colorTextSecondary),
			modifier =
			Modifier.fillMaxWidth()
				.padding(vertical = 10.dp)
				.align(Alignment.CenterStart)
				.focusRequester(focusRequester)
				.onFocusChanged { state -> onTextFieldFocused(state.isFocused) },
			shape = RoundedCornerShape(24.dp),
			keyboardOptions =
			KeyboardOptions(
				keyboardType = keyboardType,
				imeAction = ImeAction.Send,
				capitalization = KeyboardCapitalization.Sentences
			),
			keyboardActions = KeyboardActions(onSend = { onMessageSent(textFieldValue.text) })
		)
	}
}
