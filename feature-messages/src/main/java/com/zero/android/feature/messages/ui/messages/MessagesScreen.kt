package com.zero.android.feature.messages.ui.messages

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.zero.android.common.extensions.rightSwipeGesture
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.isSuccess
import com.zero.android.feature.messages.helper.MessageActionStateHandler
import com.zero.android.feature.messages.ui.components.ChatAppBar
import com.zero.android.feature.messages.ui.components.MentionUsersList
import com.zero.android.feature.messages.ui.components.ReplyMessage
import com.zero.android.feature.messages.ui.components.UserInputPanel
import com.zero.android.feature.messages.ui.mediapreview.MediaPreview
import com.zero.android.feature.messages.ui.voicememo.MemoRecorderViewModel
import com.zero.android.feature.messages.ui.voicememo.RecordMemoView
import com.zero.android.feature.messages.util.MessageUtil
import com.zero.android.models.Member
import com.zero.android.models.Message
import com.zero.android.models.enums.MessageType
import com.zero.android.ui.components.*
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import com.zero.android.ui.util.OnLifecycleEvent
import com.zero.android.ui.util.Preview
import java.io.File

@Composable
fun MessagesRoute(
	onBackClick: () -> Unit,
	onMediaClicked: (String, String) -> Unit,
	onChannelDetails: (String) -> Unit,
	viewModel: MessagesViewModel = hiltViewModel(),
	recordMemoViewModel: MemoRecorderViewModel = hiltViewModel()
) {
	val chatUiState: ChatScreenUiState by viewModel.uiState.collectAsState()
	val recordingState: Boolean by recordMemoViewModel.recordingState.collectAsState()
	val chatMentionUsers: List<Member> by viewModel.chatMentionUsers.collectAsState()
	val latestMessage by viewModel.lastMessage.collectAsState()
	val loggedInUser = viewModel.loggedInUserId

	val pagedMessages = viewModel.messages.collectAsLazyPagingItems()

	var previewMedia by remember { mutableStateOf(false) }
	var previewMediaInfo: Pair<File?, MessageType> by remember {
		mutableStateOf(Pair(null, MessageType.UNKNOWN))
	}

	val showMediaPreview: (File, MessageType) -> Unit = { file, messageType ->
		previewMediaInfo = (file to messageType)
		previewMedia = true
	}

	val showMessages: () -> Unit = {
		previewMedia = false
		previewMediaInfo = (null to MessageType.UNKNOWN)
	}

	LaunchedEffect(Unit) { viewModel.loadChannel() }
	BackHandler {
		if (MessageActionStateHandler.isActionModeStarted) {
			MessageActionStateHandler.closeActionMode()
		} else if (previewMedia) {
			showMessages()
		} else {
			MessageActionStateHandler.reset()
			onBackClick()
		}
	}

	OnLifecycleEvent { _, event ->
		if (event == Lifecycle.Event.ON_START && chatUiState.channelUiState.isSuccess) {
			pagedMessages.refresh()
		} else if (event == Lifecycle.Event.ON_RESUME && chatUiState.messagesUiState.isSuccess) {
			viewModel.configureChat()
		}
	}

	val recordMemoPermissionLauncher =
		rememberLauncherForActivityResult(
			contract = ActivityResultContracts.RequestPermission(),
			onResult = { granted -> if (granted) recordMemoViewModel.startRecording() }
		)

	if (previewMedia) {
		previewMediaInfo.first?.let {
			MediaPreview(
				mediaFile = it,
				type = previewMediaInfo.second,
				onBack = showMessages,
				sendMedia = {
					val message =
						MessageUtil.newFileMessage(
							file = it,
							authorId = loggedInUser,
							type = previewMediaInfo.second
						)
					val replyToMessage = MessageActionStateHandler.replyToMessage.value
					if (replyToMessage != null) {
						viewModel.replyToMessage(replyToMessage, message)
						MessageActionStateHandler.reset()
					} else {
						viewModel.sendMessage(message)
					}
					showMessages()
				}
			)
		}
	} else {
		MessagesScreen(
			onBackClick,
			loggedInUser,
			latestMessage,
			chatUiState.channelUiState,
			pagedMessages,
			recordingState,
			chatMentionUsers,
			onNewMessage = { newMessage ->
				viewModel.sendMessage(
					MessageUtil.newTextMessage(
						msg = newMessage.trim(),
						authorId = loggedInUser,
						channelMembers = MessageActionStateHandler.mentionedUsers
					)
				)
			},
			onMediaSelected = { mediaFile, type -> showMediaPreview(mediaFile, type) },
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
							authorId = loggedInUser,
							type = MessageType.AUDIO
						)
					val replyToMessage = MessageActionStateHandler.replyToMessage.value
					if (replyToMessage != null) {
						viewModel.replyToMessage(replyToMessage, message)
						MessageActionStateHandler.reset()
					} else {
						viewModel.sendMessage(message)
					}
				}
			},
			onRetryMessage = { viewModel.retryMessage(it) },
			onEditMessage = { viewModel.updateMessage(it) },
			onDeleteMessage = { viewModel.deleteMessage(it) },
			onReplyToMessage = { message, reply ->
				val replyMessage =
					MessageUtil.newTextMessage(
						reply.trim(),
						loggedInUser,
						channelMembers = MessageActionStateHandler.mentionedUsers
					)
				viewModel.replyToMessage(message, replyMessage)
			},
			onTextChanged = { viewModel.onSearchTextChanged(it) },
			onMediaClicked = { messageId -> onMediaClicked(viewModel.channelId, messageId) },
			onMessagesLoaded = { viewModel.configureChat() },
			onCopyMedia = { viewModel.copyMediaFile(it) },
			onChannelDetailsClick = { onChannelDetails(viewModel.channelId) }
		)
	}
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
	onBackClick: () -> Unit,
	loggedInUser: String,
	latestMessage: Message?,
	chatChannelUiState: ChannelUIState,
	messages: LazyPagingItems<Message>,
	isMemoRecording: Boolean,
	chatMentionUsers: List<Member>,
	onNewMessage: (String) -> Unit,
	onMediaSelected: (File, MessageType) -> Unit,
	onRecordMemo: () -> Unit,
	onSendMemo: () -> Unit,
	onRetryMessage: (Message) -> Unit,
	onEditMessage: (Message) -> Unit,
	onDeleteMessage: (Message) -> Unit,
	onReplyToMessage: (Message, String) -> Unit,
	onTextChanged: (String) -> Unit,
	onMediaClicked: (String) -> Unit,
	onMessagesLoaded: () -> Unit,
	onCopyMedia: (Message) -> Unit,
	onChannelDetailsClick: () -> Unit
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	val editableMessage by MessageActionStateHandler.editableMessage.collectAsState()
	val replyMessage by MessageActionStateHandler.replyToMessage.collectAsState()
	val mentionUser by MessageActionStateHandler.mentionUser.collectAsState()
	val resetScroll = remember { mutableStateOf(false) }

	if (chatChannelUiState is Result.Success) {
		Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
			Box(modifier = Modifier.rightSwipeGesture { onBackClick() }) {
				Box(modifier = Modifier.fillMaxHeight()) {
					if (messages.itemCount > 0) {
						InstantAnimation {
							MessagesContent(
								modifier = Modifier.fillMaxWidth().imePadding(),
								messages = messages,
								loggedInUser = loggedInUser,
								latestMessage = latestMessage,
								channel = chatChannelUiState.data,
								resetScroll = resetScroll.value,
								onMediaClick = onMediaClicked,
								onRetryMessage = onRetryMessage
							)
							onMessagesLoaded()
							resetScroll.value = false
						}
					}
				}
				Column(Modifier.align(Alignment.BottomCenter)) {
					BottomBarDivider()
					FadeSlideAnimation(visible = mentionUser) {
						val chatMembers = chatMentionUsers.filter { it.id != loggedInUser }
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
						Box(modifier = Modifier.height(IntrinsicSize.Max).imePadding()) {
							Box(modifier = Modifier.align(Alignment.Center).blur(20.dp).fillMaxHeight()) {
								Box(
									modifier =
									Modifier.fillMaxSize()
										.background(AppTheme.colors.surfaceInverse.copy(0.8f))
								)
							}
							UserInputPanel(
								modifier = Modifier.align(Alignment.BottomCenter),
								initialText = editableMessage?.message ?: "",
								focused = (replyMessage != null || editableMessage != null),
								onMessageSent = {
									when {
										editableMessage != null -> {
											editableMessage?.copy(message = it.trim())?.let(onEditMessage)
											MessageActionStateHandler.closeActionMode()
										}
										replyMessage != null -> {
											onReplyToMessage(replyMessage!!, it.trim())
											MessageActionStateHandler.closeActionMode()
										}
										else -> onNewMessage(it.trim())
									}
								},
								onMediaSelected = onMediaSelected,
								recordMemo = onRecordMemo,
								onTextChanged = { if (mentionUser) onTextChanged(it) },
								resetScroll = { resetScroll.value = true }
							)
						}
					}
				}
				ChatAppBar(
					modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
					channel = chatChannelUiState.data,
					loggedInUser = loggedInUser,
					onBackClick = onBackClick,
					onDeleteMessage = onDeleteMessage,
					onCopyMedia = onCopyMedia,
					onChannelDetailsClick = onChannelDetailsClick
				)
			}
		}
	}
}

@Preview @Composable
fun MessagesScreenPreview() = Preview {}
