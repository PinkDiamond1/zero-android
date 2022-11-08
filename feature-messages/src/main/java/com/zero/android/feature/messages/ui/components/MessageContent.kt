package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zero.android.common.util.SymbolAnnotationType
import com.zero.android.common.util.messageFormatter
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.feature.messages.ui.attachment.ChatAttachmentViewModel
import com.zero.android.models.Member
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.theme.AppTheme

@Composable
fun ColumnScope.MessageContent(
	message: Message,
	isUserMe: Boolean,
	chatAttachmentViewModel: ChatAttachmentViewModel,
	authorClicked: (Member) -> Unit
) {
	when (message.type) {
		MessageType.AUDIO ->
			message.fileUrl?.let {
				VoiceMessage(message = message, isUserMe = isUserMe, viewModel = chatAttachmentViewModel)
			}
		else ->
			message.message?.let {
				ClickableMessage(message = message, isUserMe = isUserMe, authorClicked = authorClicked)
			}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClickableMessage(message: Message, isUserMe: Boolean, authorClicked: (Member) -> Unit) {
	val uriHandler = LocalUriHandler.current
	val focusManager = LocalFocusManager.current
	val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

	val textColor = if (isUserMe) Color.White else AppTheme.colors.colorTextPrimary
	val styledMessage =
		(message.message ?: "").messageFormatter(
			annotationColor = textColor,
			annotationFontWeight = FontWeight.Bold
		)
	BasicText(
		modifier =
		Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp).pointerInput(Unit) {
			detectTapGestures(
				onLongPress = {
					focusManager.clearFocus()
					MessageActionStateHandler.setSelectedMessage(message)
				},
				onTap = {
					layoutResult.value?.let { layoutResult ->
						val value = layoutResult.getOffsetForPosition(it)
						styledMessage
							.getStringAnnotations(start = value, end = value)
							.firstOrNull()
							?.let { annotation ->
								when (annotation.tag) {
									SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
									SymbolAnnotationType.PERSON.name ->
										message.author?.let { author -> authorClicked(author) }
									else -> Unit
								}
							}
					}
				}
			)
		},
		text = styledMessage,
		style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
		onTextLayout = { layoutResult.value = it }
	)
}
