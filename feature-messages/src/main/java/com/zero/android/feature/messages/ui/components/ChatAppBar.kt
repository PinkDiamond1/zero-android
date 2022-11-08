package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.models.Channel
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.SmallClickableIcon
import com.zero.android.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
	modifier: Modifier,
	channel: Channel,
	loggedInUser: String,
	scrollBehavior: TopAppBarScrollBehavior? = null,
	onBackClick: () -> Unit,
	onDeleteMessage: (Message) -> Unit,
	onCopyMedia: (Message) -> Unit,
	onChannelDetailsClick: () -> Unit
) {
	val actionMessage by MessageActionStateHandler.selectedMessage.collectAsState()
	val clipboardManager = LocalClipboardManager.current

	Box(
		modifier =
		modifier
			.fillMaxWidth()
			.background(color = AppTheme.colors.surfaceInverse.copy(alpha = 0.8f))
	) {
		Box(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
			Row(
				modifier = Modifier.align(Alignment.CenterStart),
				verticalAlignment = Alignment.CenterVertically
			) {
				IconButton(
					onClick = {
						if (actionMessage != null) {
							MessageActionStateHandler.closeActionMode()
						} else {
							MessageActionStateHandler.reset()
							onBackClick()
						}
					}
				) {
					Icon(
						imageVector = Icons.Filled.ArrowBack,
						contentDescription = "",
						tint = AppTheme.colors.glow
					)
				}
				ChatScreenAppBarTitle(channel)
			}
			Row(
				modifier = Modifier.align(Alignment.CenterEnd),
				verticalAlignment = Alignment.CenterVertically
			) {
				// 			if (actionMessage == null) {
				// 				SmallClickableIcon(
				// 					icon = R.drawable.ic_more_vertical,
				// 					contentDescription = "Channel Info",
				// 					onClick = onChannelDetailsClick
				// 				)
				// 			} else {
				if (actionMessage != null) {
					val message = actionMessage!!
					if ((message.type == MessageType.IMAGE) ||
						(message.type == MessageType.TEXT && !message.message.isNullOrEmpty())
					) {
						SmallClickableIcon(
							icon = R.drawable.ic_copy_24,
							contentDescription = "Copy",
							onClick = {
								if (message.type == MessageType.IMAGE) {
									onCopyMedia(message)
								} else {
									clipboardManager.setText(AnnotatedString(message.message!!))
								}
								MessageActionStateHandler.closeActionMode()
							}
						)
					}
					if (!message.isReply) {
						SmallClickableIcon(
							icon = R.drawable.ic_reply_24,
							contentDescription = "Reply",
							onClick = { MessageActionStateHandler.replyToMessage() }
						)
					}
					if (message.author?.id == loggedInUser) {
						if (message.type !in MessageType.mediaMessageTypes) {
							SmallClickableIcon(
								vector = Icons.Filled.Edit,
								contentDescription = "Edit",
								onClick = { MessageActionStateHandler.editTextMessage() }
							)
						}
						SmallClickableIcon(
							vector = Icons.Filled.Delete,
							contentDescription = "Delete",
							onClick = {
								message.let(onDeleteMessage)
								MessageActionStateHandler.closeActionMode()
							}
						)
					}
				}
			}
		}
	}
}
