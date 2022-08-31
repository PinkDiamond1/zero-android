package com.zero.android.feature.messages.ui.messages

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.dhaval2404.imagepicker.ImagePicker
import com.zero.android.common.R
import com.zero.android.common.extensions.getActivity
import com.zero.android.common.extensions.toFile
import com.zero.android.common.ui.Result
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.feature.messages.ui.components.ChatScreenAppBarTitle
import com.zero.android.feature.messages.ui.components.MentionUsersList
import com.zero.android.feature.messages.ui.components.ReplyMessage
import com.zero.android.feature.messages.ui.components.UserInputPanel
import com.zero.android.feature.messages.ui.voicememo.MemoRecorderViewModel
import com.zero.android.feature.messages.ui.voicememo.RecordMemoView
import com.zero.android.feature.messages.util.MessageUtil
import com.zero.android.models.Member
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.components.BottomBarDivider
import com.zero.android.ui.components.FadeExpandAnimation
import com.zero.android.ui.components.FadeSlideAnimation
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import java.io.File

@Composable
fun MessagesRoute(
	onBackClick: () -> Unit,
	viewModel: MessagesViewModel = hiltViewModel(),
	recordMemoViewModel: MemoRecorderViewModel = hiltViewModel()
) {
	val chatUiState: ChatScreenUiState by viewModel.uiState.collectAsState()
	val recordingState: Boolean by recordMemoViewModel.recordingState.collectAsState()
	val chatMentionUsers: List<Member> by viewModel.chatMentionUsers.collectAsState()
	val userChannelInfo = viewModel.loggedInUserId to viewModel.isGroupChannel
	val context = LocalContext.current

	val pagedMessages = viewModel.messages.collectAsLazyPagingItems()

	LaunchedEffect(Unit) { viewModel.loadChannel() }
	BackHandler {
		if (MessageActionStateHandler.isActionModeStarted) {
			MessageActionStateHandler.closeActionMode()
		} else {
			MessageActionStateHandler.reset()
			onBackClick()
		}
	}

	val imageSelectorLauncher =
		rememberLauncherForActivityResult(
			contract = ActivityResultContracts.StartActivityForResult(),
			onResult = {
				val resultCode = it.resultCode
				val data = it.data
				if (resultCode == Activity.RESULT_OK) {
					// Image Uri will not be null for RESULT_OK
					val fileUri = data?.data!!
					val file =
						try {
							fileUri.toFile()
						} catch (e: Exception) {
							fileUri.toFile(context)
						}
					val message =
						MessageUtil.newFileMessage(
							file = file,
							authorId = userChannelInfo.first,
							type = MessageType.IMAGE
						)
					val replyToMessage = MessageActionStateHandler.replyToMessage.value
					if (replyToMessage != null) {
						viewModel.replyToMessage(replyToMessage.id, message)
						MessageActionStateHandler.reset()
					} else {
						viewModel.sendMessage(message)
					}
				}
			}
		)
	val recordMemoPermissionLauncher =
		rememberLauncherForActivityResult(
			contract = ActivityResultContracts.RequestPermission(),
			onResult = { granted -> if (granted) recordMemoViewModel.startRecording() }
		)

	MessagesScreen(
		onBackClick,
		userChannelInfo,
		chatUiState.channelUiState,
		chatUiState.messagesUiState,
		pagedMessages,
		recordingState,
		chatMentionUsers,
		onNewMessage = { newMessage ->
			viewModel.sendMessage(
				MessageUtil.newTextMessage(
					msg = newMessage,
					authorId = userChannelInfo.first,
					channelMembers = MessageActionStateHandler.mentionedUsers
				)
			)
		},
		onPickImage = { imageSelectorLauncher.launch(it) },
		onRecordMemo = {
			val isRecording = recordMemoViewModel.recordingState.value
			if (isRecording) {
				recordMemoViewModel.stopRecording()
			} else {
				recordMemoPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
			}
		},
		onSendMemo = {
			recordMemoViewModel.stopRecording()
			val file = File(recordMemoViewModel.lastMemoPath)
			if (file.exists()) {
				val message =
					MessageUtil.newFileMessage(
						file = file,
						authorId = userChannelInfo.first,
						type = MessageType.AUDIO
					)
				val replyToMessage = MessageActionStateHandler.replyToMessage.value
				if (replyToMessage != null) {
					viewModel.replyToMessage(replyToMessage.id, message)
					MessageActionStateHandler.reset()
				} else {
					viewModel.sendMessage(message)
				}
			}
		},
		onEditMessage = { viewModel.updateMessage(it) },
		onDeleteMessage = { viewModel.deleteMessage(it) },
		onReplyToMessage = { messageId, reply ->
			val replyMessage =
				MessageUtil.newTextMessage(
					reply,
					userChannelInfo.first,
					channelMembers = MessageActionStateHandler.mentionedUsers
				)
			viewModel.replyToMessage(messageId, replyMessage)
		},
		onTextChanged = { viewModel.onSearchTextChanged(it) }
	)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
	onBackClick: () -> Unit,
	userChannelInfo: Pair<String, Boolean>,
	chatChannelUiState: ChannelUIState,
	messagesUiState: MessagesUIState,
	messages: LazyPagingItems<Message>,
	isMemoRecording: Boolean,
	chatMentionUsers: List<Member>,
	onNewMessage: (String) -> Unit,
	onPickImage: (Intent) -> Unit,
	onRecordMemo: () -> Unit,
	onSendMemo: () -> Unit,
	onEditMessage: (Message) -> Unit,
	onDeleteMessage: (Message) -> Unit,
	onReplyToMessage: (String, String) -> Unit,
	onTextChanged: (String) -> Unit
) {
	val context = LocalContext.current
	val actionMessage by MessageActionStateHandler.selectedMessage.collectAsState()
	val editableMessage by MessageActionStateHandler.editableMessage.collectAsState()
	val replyMessage by MessageActionStateHandler.replyToMessage.collectAsState()
	val mentionUser by MessageActionStateHandler.mentionUser.collectAsState()

	if (chatChannelUiState is Result.Success) {
		val topBar: @Composable () -> Unit = {
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
						ChatScreenAppBarTitle(chatChannelUiState.data, userChannelInfo.second)
					}
				},
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
						IconButton(onClick = {}) {
							Icon(
								painter = painterResource(R.drawable.ic_search),
								contentDescription = "cd_search_message"
							)
						}
						IconButton(onClick = {}) {
							Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "cd_more_options")
						}
					}
				}
			)
		}
		Scaffold(topBar = { topBar() }) {
			Box(Modifier.padding(it)) {
				MessagesContent(
					modifier = Modifier.fillMaxWidth(),
					userChannelInfo = userChannelInfo,
					uiState = messagesUiState,
					messages = messages
				)
				Column(Modifier.align(Alignment.BottomCenter)) {
					BottomBarDivider()
					FadeSlideAnimation(visible = mentionUser) {
						val chatMembers = chatMentionUsers.filter { it.id != userChannelInfo.first }
						MentionUsersList(
							membersList = chatMembers,
							onMemberSelected = { MessageActionStateHandler.onUserMentionSelected(it) }
						)
					}
					FadeExpandAnimation(visible = replyMessage != null) {
						replyMessage?.let {
							ReplyMessage(message = it, color = AppTheme.colors.surfaceInverse) {
								MessageActionStateHandler.closeActionMode()
							}
						}
					}
					FadeSlideAnimation(visible = isMemoRecording) {
						RecordMemoView(onCancel = onRecordMemo, onSendMemo = onSendMemo)
					}
					if (!isMemoRecording) {
						Box(modifier = Modifier.height(IntrinsicSize.Max)) {
							Box(modifier = Modifier.align(Alignment.Center).blur(20.dp).fillMaxHeight()) {
								Box(
									modifier =
									Modifier.fillMaxSize()
										.background(AppTheme.colors.surfaceInverse.copy(0.8f))
								)
							}
							if (editableMessage != null) {
								UserInputPanel(
									modifier = Modifier.align(Alignment.BottomCenter),
									initialText = editableMessage?.message ?: "",
									onMessageSent = {
										if (editableMessage != null) {
											editableMessage?.copy(message = it.trim())?.let(onEditMessage)
											MessageActionStateHandler.closeActionMode()
										} else onNewMessage(it.trim())
									},
									addAttachment = {
										context.getActivity()?.let { showImagePicker(false, it, onPickImage) }
									},
									addImage = {
										context.getActivity()?.let { showImagePicker(true, it, onPickImage) }
									},
									recordMemo = onRecordMemo,
									onTextChanged = { if (mentionUser) onTextChanged(it) }
								)
							} else {
								UserInputPanel(
									modifier = Modifier.align(Alignment.BottomCenter),
									onMessageSent = {
										if (replyMessage != null) {
											onReplyToMessage(replyMessage!!.id, it.trim())
											MessageActionStateHandler.closeActionMode()
										} else onNewMessage(it.trim())
									},
									addAttachment = {
										context.getActivity()?.let { showImagePicker(false, it, onPickImage) }
									},
									addImage = {
										context.getActivity()?.let { showImagePicker(true, it, onPickImage) }
									},
									recordMemo = onRecordMemo,
									onTextChanged = { if (mentionUser) onTextChanged(it) }
								)
							}
						}
					}
				}
			}
		}
	}
}

private fun showImagePicker(
	fromCamera: Boolean = false,
	activity: Activity,
	onImagePicker: (Intent) -> Unit
) {
	ImagePicker.with(activity).apply {
		if (fromCamera) cameraOnly() else galleryOnly()
		createIntent { onImagePicker(it) }
	}
}

@Preview @Composable
fun MessagesScreenPreview() = Preview {}
