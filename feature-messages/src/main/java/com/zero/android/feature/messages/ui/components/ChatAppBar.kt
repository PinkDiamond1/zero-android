package com.zero.android.feature.messages.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.zero.android.common.R
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.models.Channel
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
	channel: Channel,
	userChannelInfo: Pair<String, Boolean>,
	scrollBehavior: TopAppBarScrollBehavior? = null,
	onBackClick: () -> Unit,
	onDeleteMessage: (Message) -> Unit
) {
	val actionMessage by MessageActionStateHandler.selectedMessage.collectAsState()

	AppBar(
		navIcon = {
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
					contentDescription = "cd_back",
					tint = AppTheme.colors.glow
				)
			}
		},
		title = {
			if (actionMessage == null) {
				ChatScreenAppBarTitle(channel, userChannelInfo.second)
			}
		},
		scrollBehavior = scrollBehavior,
		actions = {
			if (actionMessage != null) {
				if (actionMessage?.isReply == false) {
					IconButton(onClick = { MessageActionStateHandler.replyToMessage() }) {
						Icon(
							painter = painterResource(R.drawable.ic_reply_24),
							contentDescription = "cd_message_action_reply",
							tint = AppTheme.colors.surface
						)
					}
				}
				if (actionMessage!!.author?.id == userChannelInfo.first) {
					if (actionMessage!!.type == MessageType.TEXT) {
						IconButton(onClick = { MessageActionStateHandler.editTextMessage() }) {
							Icon(
								imageVector = Icons.Filled.Edit,
								contentDescription = "cd_message_action_edit",
								tint = AppTheme.colors.surface
							)
						}
					}
					IconButton(
						onClick = {
							actionMessage?.let(onDeleteMessage)
							MessageActionStateHandler.closeActionMode()
						}
					) {
						Icon(
							imageVector = Icons.Filled.Delete,
							contentDescription = "cd_message_action_delete",
							tint = AppTheme.colors.surface
						)
					}
				}
			} else {
          /*IconButton(onClick = {}) {
              Icon(
                  painter = painterResource(R.drawable.ic_search),
                  contentDescription = "cd_search_message"
              )
          }
          IconButton(onClick = {}) {
              Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "cd_more_options")
          }*/
			}
		}
	)
}
