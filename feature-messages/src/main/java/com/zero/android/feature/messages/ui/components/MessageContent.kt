package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zero.android.common.util.SymbolAnnotationType
import com.zero.android.common.util.messageFormatter
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

@Composable
private fun ClickableMessage(message: Message, isUserMe: Boolean, authorClicked: (Member) -> Unit) {
	val uriHandler = LocalUriHandler.current
	val styledMessage =
		(message.message ?: "").messageFormatter(
			annotationColor = AppTheme.colors.glow,
			annotationFontWeight = FontWeight.Bold
		)
	val textColor = if (isUserMe) Color.White else AppTheme.colors.colorTextPrimary
	ClickableText(
		modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp),
		text = styledMessage,
		style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
		onClick = {
			styledMessage.getStringAnnotations(start = it, end = it).firstOrNull()?.let { annotation ->
				when (annotation.tag) {
					SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
					SymbolAnnotationType.PERSON.name ->
						message.author?.let { author -> authorClicked(author) }
					else -> Unit
				}
			}
		}
	)
}
