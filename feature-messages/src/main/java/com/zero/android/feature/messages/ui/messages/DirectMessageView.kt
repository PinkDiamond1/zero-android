package com.zero.android.feature.messages.ui.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zero.android.common.R
import com.zero.android.common.extensions.format
import com.zero.android.common.extensions.toDate
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.feature.messages.ui.attachment.ChatAttachmentViewModel
import com.zero.android.feature.messages.ui.components.ImageMessage
import com.zero.android.feature.messages.ui.components.MessageContent
import com.zero.android.feature.messages.ui.components.ReplyMessage
import com.zero.android.feature.messages.ui.components.VideoMessage
import com.zero.android.models.Member
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.SmallCircularImage
import com.zero.android.ui.theme.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DirectMessage(
	modifier: Modifier = Modifier,
	msg: Message,
	isUserMe: Boolean,
	isSameDay: Boolean,
	isFirstMessageByAuthor: Boolean,
	isLastMessageByAuthor: Boolean,
	showDeliveryStatus: Boolean,
	chatAttachmentViewModel: ChatAttachmentViewModel,
	onAuthorClick: (Member) -> Unit,
	onMediaClick: (String) -> Unit
) {
	val focusManager = LocalFocusManager.current
	val currentSelectedMessage: Message? by MessageActionStateHandler.selectedMessage.collectAsState()
	val mModifier = if (isLastMessageByAuthor) modifier.padding(top = 8.dp) else modifier
	Column(
		modifier =
		mModifier
			.fillMaxWidth()
			.combinedClickable(
				onClick = {
					focusManager.clearFocus(true)
					if (msg.type == MessageType.IMAGE || msg.type == MessageType.VIDEO) {
						onMediaClick(msg.id)
					}
				},
				onLongClick = {
					focusManager.clearFocus(true)
					MessageActionStateHandler.setSelectedMessage(msg)
				}
			)
	) {
		Row(
			modifier =
			if (currentSelectedMessage?.id == msg.id) {
				Modifier.fillMaxWidth()
					.background(AppTheme.colors.surface.copy(0.1f))
					.padding(horizontal = 12.dp)
			} else Modifier.fillMaxWidth().padding(horizontal = 12.dp),
			horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
		) {
			if (!isUserMe && (isLastMessageByAuthor || !isSameDay)) {
				SmallCircularImage(
					modifier = Modifier.align(Alignment.Bottom).padding(bottom = 4.dp),
					imageUrl = msg.author?.profileImage,
					placeHolder = R.drawable.ic_user_profile_placeholder
				)
			} else {
				Spacer(modifier = Modifier.width(36.dp))
			}
			when (msg.type) {
				MessageType.IMAGE ->
					msg.fileUrl?.let { ImageMessage(it, msg.createdAt, isUserMe, isFirstMessageByAuthor) }
				MessageType.VIDEO -> {
					msg.fileUrl?.let { VideoMessage(it, msg.createdAt, isUserMe, isFirstMessageByAuthor) }
				}
				else ->
					DMAuthorAndTextMessage(
						modifier = Modifier.padding(end = 16.dp).weight(1f),
						message = msg,
						isUserMe = isUserMe,
						isSameDay = isSameDay,
						isFirstMessageByAuthor = isFirstMessageByAuthor,
						isLastMessageByAuthor = isLastMessageByAuthor,
						authorClicked = onAuthorClick,
						chatAttachmentViewModel = chatAttachmentViewModel
					)
			}
		}
		AnimatedVisibility(
			visible = showDeliveryStatus,
			modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
		) {
			Text(
				style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
				text = msg.deliveryStatus.name.lowercase().replaceFirstChar { it.uppercase() },
				color = AppTheme.colors.colorTextSecondaryVariant
			)
		}
	}
}

private val ChatDirectOther = RoundedCornerShape(12.dp, 12.dp, 12.dp, 4.dp)
private val ChatDirectAuthor = RoundedCornerShape(12.dp, 12.dp, 4.dp, 12.dp)
private val ChatDirectSame = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)

@Composable
fun DMAuthorAndTextMessage(
	modifier: Modifier = Modifier,
	message: Message,
	isUserMe: Boolean,
	isSameDay: Boolean,
	isFirstMessageByAuthor: Boolean,
	isLastMessageByAuthor: Boolean,
	chatAttachmentViewModel: ChatAttachmentViewModel,
	authorClicked: (Member) -> Unit
) {
	val backgroundColorsList =
		if (isUserMe) {
			listOf(AppTheme.colors.glowVariant, AppTheme.colors.glow)
		} else {
			listOf(AppTheme.colors.chatBubblePrimaryVariant, AppTheme.colors.chatBubblePrimary)
		}
	Column {
		Row {
			Spacer(modifier = Modifier.width(12.dp))
			Box(
				modifier =
				Modifier.background(
					brush = Brush.linearGradient(colors = backgroundColorsList),
					shape =
					if (isLastMessageByAuthor || !isSameDay) {
						if (isUserMe) ChatDirectAuthor else ChatDirectOther
					} else ChatDirectSame
				)
			) {
				Column(modifier = Modifier.padding(4.dp)) {
					message.parentMessage?.let {
						ReplyMessage(
							modifier = Modifier.wrapContentWidth(),
							message = it,
							showCloseButton = false
						)
					}
					if (!isUserMe && (isLastMessageByAuthor || !isSameDay)) {
						Text(
							text = message.author?.name ?: "",
							style = MaterialTheme.typography.titleMedium,
							color = AppTheme.colors.glow,
							modifier =
							Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp)
								.paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
						)
						Spacer(modifier = Modifier.width(8.dp))
					}
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
						modifier =
						Modifier.align(Alignment.End).padding(start = 4.dp, end = 4.dp, bottom = 2.dp),
						color = if (isUserMe) Color.White else AppTheme.colors.colorTextSecondary
					)
				}
			}
		}
		ChatBubbleSpacing(isFirstMessageByAuthor)
	}
}

@Composable
fun ColumnScope.ChatBubbleSpacing(isFirstMessageByAuthor: Boolean) {
	if (isFirstMessageByAuthor) {
		// Last bubble before next author
		Spacer(modifier = Modifier.height(6.dp))
	} else {
		// Between bubbles
		Spacer(modifier = Modifier.height(3.dp))
	}
}
