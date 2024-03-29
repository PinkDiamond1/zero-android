package com.zero.android.feature.messages.ui.messages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zero.android.common.extensions.format
import com.zero.android.common.extensions.toDate
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.feature.messages.ui.attachment.ChatAttachmentViewModel
import com.zero.android.feature.messages.ui.components.MessageContent
import com.zero.android.feature.messages.ui.components.ReplyMessage
import com.zero.android.models.Member
import com.zero.android.models.Message
import com.zero.android.ui.components.Avatar
import com.zero.android.ui.theme.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChannelMessage(
	msg: Message,
	isUserMe: Boolean,
	isFirstMessageByAuthor: Boolean,
	chatAttachmentViewModel: ChatAttachmentViewModel,
	onAuthorClick: (Member) -> Unit
) {
	val currentSelectedMessage: Message? by MessageActionStateHandler.selectedMessage.collectAsState()
	val modifier =
		Modifier.fillMaxWidth()
			.combinedClickable(
				onClick = {},
				onLongClick = { MessageActionStateHandler.setSelectedMessage(msg) }
			)
	Row(
		modifier =
		if (currentSelectedMessage?.id == msg.id) {
			modifier.background(Color.White.copy(0.1f))
		} else modifier
	) {
		Avatar(size = 36.dp, user = msg.author)
		CMAuthorAndTextMessage(
			modifier = Modifier.padding(end = 16.dp).weight(1f),
			message = msg,
			isUserMe = isUserMe,
			isFirstMessageByAuthor = isFirstMessageByAuthor,
			authorClicked = onAuthorClick,
			chatAttachmentViewModel = chatAttachmentViewModel
		)
	}
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 12.dp, 12.dp, 12.dp)

@Composable
fun CMAuthorAndTextMessage(
	modifier: Modifier = Modifier,
	message: Message,
	isUserMe: Boolean,
	chatAttachmentViewModel: ChatAttachmentViewModel,
	isFirstMessageByAuthor: Boolean,
	authorClicked: (Member) -> Unit
) {
	val backgroundColorsList =
		if (isUserMe) {
			listOf(Color(0xFF470080), Color(0xFFB14EFF))
		} else {
			listOf(Color(0xFF191919), Color(0xFF0A0A0A))
		}
	Column {
		Row {
			Spacer(modifier = Modifier.width(12.dp))
			Box(
				modifier =
				Modifier.background(
					brush = Brush.linearGradient(colors = backgroundColorsList),
					shape = ChatBubbleShape
				)
			) {
				Column(modifier = Modifier.padding(8.dp)) {
					message.parentMessage?.let {
						ReplyMessage(
							modifier = Modifier.wrapContentWidth(),
							message = it,
							showCloseButton = false
						)
					}
					AuthorNameTimestamp(isUserMe, message)
					MessageContent(
						message = message,
						isUserMe = isUserMe,
						authorClicked = authorClicked,
						chatAttachmentViewModel = chatAttachmentViewModel
					)
					val messageDate = message.createdAt.toDate()
					Text(
						text = messageDate.format("hh:mm aa"),
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.align(Alignment.End),
						color = if (isUserMe) Color.White else AppTheme.colors.colorTextSecondary
					)
				}
			}
		}
		ChatBubbleSpacing(isFirstMessageByAuthor)
	}
}

@Composable
private fun AuthorNameTimestamp(isUserMe: Boolean, msg: Message) {
	Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
		Text(
			text = if (isUserMe) "Me" else msg.author?.name ?: "",
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.primary,
			modifier =
			Modifier.alignBy(LastBaseline)
				.paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
		)
    /*Spacer(modifier = Modifier.width(8.dp))
    val messageDate = msg.createdAt.toDate()
    Text(
    	text = "${messageDate.toMessageDateFormat()} at ${messageDate.format("hh:mm aa")}",
    	style = MaterialTheme.typography.bodySmall,
    	modifier = Modifier.alignBy(LastBaseline),
    	color = if (isUserMe) Color.White else AppTheme.colors.colorTextSecondary
    )*/
	}
}
