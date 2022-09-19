package com.zero.android.feature.messages.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.zero.android.common.extensions.format
import com.zero.android.common.extensions.isSameDay
import com.zero.android.common.extensions.toDate
import com.zero.android.feature.messages.ui.attachment.ChatAttachmentViewModel
import com.zero.android.models.Message
import com.zero.android.ui.components.JumpToBottom
import com.zero.android.ui.components.StrikeLabel
import com.zero.android.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesContent(
	modifier: Modifier = Modifier,
	userChannelInfo: Pair<String, Boolean>,
	messages: LazyPagingItems<Message>,
	onMediaClick: (String) -> Unit
) {
	val scrollState = rememberLazyListState()
	val scope = rememberCoroutineScope()

	Surface(modifier = modifier) {
		Box(modifier = Modifier.fillMaxSize()) {
			Column(Modifier.fillMaxSize().background(AppTheme.colors.surfaceInverse)) {
				Messages(
					modifier = Modifier.weight(1f),
					userChannelInfo = userChannelInfo,
					messages = messages,
					scrollState = scrollState,
					coroutineScope = scope,
					onMediaClick = onMediaClick
				)
			}
		}
	}
}

@Composable
fun Messages(
	modifier: Modifier = Modifier,
	userChannelInfo: Pair<String, Boolean>,
	messages: LazyPagingItems<Message>,
	scrollState: LazyListState,
	coroutineScope: CoroutineScope,
	chatAttachmentViewModel: ChatAttachmentViewModel = hiltViewModel(),
	onMediaClick: (String) -> Unit
) {
	DisposableEffect(Unit) { onDispose { chatAttachmentViewModel.dispose() } }
	Box {
		chatAttachmentViewModel.configure(messages)
		LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true, state = scrollState) {
			item { Spacer(modifier = Modifier.size(100.dp)) }
			items(messages) { content ->
				content ?: return@items
				val index = messages.itemSnapshotList.items.indexOf(content)

				val prevAuthor = if (index != 0) messages[index - 1]?.author else null
				val nextAuthor = if (messages.itemCount > index + 1) messages[index + 1]?.author else null
				val messageDate = content.createdAt.toDate()
				val nextMessageDate =
					if (messages.itemCount > index + 1) (messages[index + 1]?.createdAt ?: 0).toDate()
					else 0L.toDate()
				val isSameDay = nextMessageDate.isSameDay(messageDate)
				val isFirstMessageByAuthor = prevAuthor?.id != content.author?.id
				val isLastMessageByAuthor = nextAuthor?.id != content.author?.id

        /*if (!userChannelInfo.second) {
            DirectMessage(
                msg = content,
                isUserMe = content.author.id == userChannelInfo.first,
                isSameDay = isSameDay,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                isLastMessageByAuthor = isLastMessageByAuthor,
                chatAttachmentViewModel = chatAttachmentViewModel,
                onAuthorClick = {}
            )
        } else {
            ChannelMessage(
                msg = content,
                isUserMe = content.author.id == userChannelInfo.first,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                chatAttachmentViewModel = chatAttachmentViewModel,
                onAuthorClick = {}
            )
        }*/
				DirectMessage(
					msg = content,
					isUserMe = content.author?.id == userChannelInfo.first,
					isSameDay = isSameDay,
					isFirstMessageByAuthor = isFirstMessageByAuthor,
					isLastMessageByAuthor = isLastMessageByAuthor,
					chatAttachmentViewModel = chatAttachmentViewModel,
					onAuthorClick = {},
					onMediaClick = onMediaClick
				)

				if (!isSameDay) {
					StrikeLabel(messageDate.format("MMMM dd, yyyy"))
				}
			}
		}

		val jumpThreshold = with(LocalDensity.current) { JumpToBottomThreshold.toPx() }
		val jumpToBottomButtonEnabled by remember {
			derivedStateOf {
				scrollState.firstVisibleItemIndex != 0 ||
					scrollState.firstVisibleItemScrollOffset > jumpThreshold
			}
		}
		JumpToBottom(
			// Only show if the scroller is not at the bottom
			enabled = jumpToBottomButtonEnabled,
			onClicked = { coroutineScope.launch { scrollState.animateScrollToItem(0) } },
			modifier = Modifier.align(Alignment.BottomCenter)
		)
	}
}

@Preview @Composable
fun ConversationPreview() {}

private val JumpToBottomThreshold = 56.dp
