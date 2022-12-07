package com.zero.android.feature.channels.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.common.ui.Result
import com.zero.android.feature.channels.ui.components.ChannelNotificationSettingsView
import com.zero.android.feature.people.ui.components.MemberListItem
import com.zero.android.models.Channel
import com.zero.android.models.ChatMedia
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.Member
import com.zero.android.models.enums.AlertType
import com.zero.android.models.enums.MessageType
import com.zero.android.models.fake.FakeModel
import com.zero.android.navigation.util.NavigationState
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.components.LoadingContainer
import com.zero.android.ui.components.RoundedImage
import com.zero.android.ui.components.TextIconListItem
import com.zero.android.ui.extensions.bodyPaddings
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.BODY_PADDING_HORIZONTAL
import com.zero.android.ui.theme.Gray
import com.zero.android.ui.util.BackHandler
import com.zero.android.ui.util.Preview
import kotlinx.coroutines.launch

@Composable
fun ChannelDetailsRoute(
	viewModel: ChannelDetailsViewModel = hiltViewModel(),
	onBackClick: () -> Unit,
	onEditClick: (String) -> Unit,
	onAddMember: (String) -> Unit,
	onLeaveChannel: () -> Unit,
	onMediaClick: (String, ChatMedia) -> Unit
) {
	val channel by viewModel.channel.collectAsState()
	val chatMedia by viewModel.chatMedia.collectAsState(emptyList())

	val navState by viewModel.navState.collectAsState()

	LaunchedEffect(navState) { if (navState is NavigationState.Navigate) onLeaveChannel() }

	ChannelDetailsScreen(
		channelResult = channel,
		chatMedia = chatMedia,
		onBackClick = onBackClick,
		onEditClick = { onEditClick(viewModel.channelId) },
		onMediaClick = { onMediaClick(viewModel.channelId, it) },
		onAddMember = { onAddMember(viewModel.channelId) },
		updateAlerts = { viewModel.updateAlerts(it) },
		leaveChannel = { viewModel.leaveChannel() }
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChannelDetailsScreen(
	channelResult: Result<Channel>,
	chatMedia: List<ChatMedia>,
	onEditClick: () -> Unit,
	onMediaClick: (ChatMedia) -> Unit,
	onBackClick: () -> Unit,
	onMemberClick: (Member) -> Unit = {},
	onAddMember: () -> Unit,
	updateAlerts: (AlertType) -> Unit,
	leaveChannel: () -> Unit
) {
	val channel = if (channelResult is Result.Success) channelResult.data else null
	val operatorIds = if (channel is GroupChannel) channel.operators.map { it.id } else null
	val isLoading = channelResult is Result.Loading

	val coroutineScope = rememberCoroutineScope()
	val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

	BackHandler {
		if (bottomState.isVisible) coroutineScope.launch { bottomState.hide() } else onBackClick()
	}

	val isOneToOne = channel is DirectChannel && channel.isOneToOne

	val topBar: @Composable () -> Unit = {
		AppBar(
			centered = true,
			navIcon = {
				IconButton(onClick = { onBackClick() }) {
					Icon(
						imageVector = Icons.Filled.ArrowBack,
						contentDescription = "cd_back",
						tint = AppTheme.colors.glow
					)
				}
			},
			title = {
				Text(
					text = stringResource(R.string.channel_info),
					style = MaterialTheme.typography.displayLarge
				)
			},
			actions = {
				if (!isOneToOne) {
					TextButton(
						colors = ButtonDefaults.textButtonColors(contentColor = AppTheme.colors.glow),
						onClick = onEditClick
					) {
						Text(text = stringResource(R.string.edit), fontSize = 12.sp)
					}
				}
			}
		)
	}

	val channelInfo: @Composable ColumnScope.() -> Unit = {
		Column(
			modifier = Modifier.align(Alignment.CenterHorizontally),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			CircularInitialsImage(size = 64.dp, name = channel?.name ?: "", url = channel?.image)
			Row(modifier = Modifier.padding(top = 8.dp)) {
				Text(
					text = channel?.name ?: "",
					color = AppTheme.colors.colorTextPrimary,
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Medium,
					modifier = Modifier.align(Alignment.CenterVertically),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				if (channel is GroupChannel) {
					channel.icon?.let { icon ->
						Spacer(modifier = Modifier.padding(4.dp))
						Image(
							painter = painterResource(icon),
							contentDescription = "",
							modifier = Modifier.wrapContentSize().align(Alignment.CenterVertically),
							contentScale = ContentScale.Fit,
							colorFilter = ColorFilter.tint(AppTheme.colors.colorTextPrimary)
						)
						Spacer(modifier = Modifier.padding(4.dp))
					}
				}
			}
			channel?.description?.let {
				Text(
					modifier = Modifier.padding(horizontal = 16.dp),
					text = channel.description ?: "",
					color = AppTheme.colors.colorTextSecondary,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium,
					overflow = TextOverflow.Ellipsis,
					textAlign = TextAlign.Center
				)
			}
		}
	}

	val channelMedia: @Composable ColumnScope.() -> Unit = {
		Column {
			Text(
				modifier = Modifier.bodyPaddings(vertical = 0f),
				text = stringResource(R.string.channel_media),
				style = MaterialTheme.typography.displayLarge,
				fontWeight = FontWeight.SemiBold
			)
			if (chatMedia.isNotEmpty()) {
				LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
					items(chatMedia) { media ->
						if (media.type == MessageType.IMAGE || media.type == MessageType.VIDEO) {
							RoundedImage(
								modifier =
								Modifier.padding(
									start = BODY_PADDING_HORIZONTAL.dp,
									end =
									if (chatMedia.indexOf(media) == chatMedia.size) {
										BODY_PADDING_HORIZONTAL.dp
									} else 0.dp
								)
									.clickable { onMediaClick(media) },
								size = 80.dp,
								radius = 16,
								url = media.mediaUrl,
								placeHolder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
							)
						}
					}
				}
				Text(
					modifier =
					Modifier.bodyPaddings(vertical = 0f).clickable {
						chatMedia
							.find { it.type == MessageType.IMAGE || it.type == MessageType.VIDEO }
							?.let { onMediaClick(it) }
					},
					text = stringResource(R.string.media_see_all),
					style = MaterialTheme.typography.displayMedium
				)
			} else {
				Text(
					modifier = Modifier.bodyPaddings(vertical = 0f),
					text = stringResource(R.string.msg_media_empty),
					style = MaterialTheme.typography.displaySmall
				)
			}
		}
	}

	val channelMembers: @Composable ColumnScope.() -> Unit = {
		Column {
			Text(
				modifier = Modifier.bodyPaddings(vertical = 0f),
				text = stringResource(R.string.members_count, channel?.memberCount ?: 0),
				style = MaterialTheme.typography.displayLarge,
				fontWeight = FontWeight.SemiBold
			)

			Column(modifier = Modifier.padding(top = 8.dp)) {
				Row(
					modifier =
					Modifier.clickable { onAddMember() }
						.padding(horizontal = BODY_PADDING_HORIZONTAL.dp, vertical = 8.dp)
						.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					if (!isOneToOne) {
						Box(modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
							Icon(
								modifier = Modifier.size(26.dp),
								painter = painterResource(R.drawable.ic_add_circle),
								contentDescription = "ic_add",
								tint = AppTheme.colors.glow
							)
						}
						Text(
							modifier = Modifier.padding(start = 8.dp),
							text = stringResource(R.string.add_member),
							color = AppTheme.colors.glow,
							style = MaterialTheme.typography.bodyMedium,
							overflow = TextOverflow.Ellipsis
						)
					}
				}

				channel?.members?.forEach { member ->
					MemberListItem(
						member = member,
						onClick = onMemberClick,
						endView = {
							if (operatorIds?.contains(member.id) == true) {
								Text(
									text = stringResource(R.string.user_role_admin),
									style = MaterialTheme.typography.labelSmall,
									color = Gray
								)
							}
						}
					)
				}
			}
		}
	}

	val alertsDialogView: @Composable ColumnScope.() -> Unit = {
		ChannelNotificationSettingsView { alertType ->
			updateAlerts(alertType)
			coroutineScope.launch { bottomState.hide() }
		}
	}

	ModalBottomSheetLayout(
		sheetState = bottomState,
		sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
		sheetContent = alertsDialogView
	) {
		Scaffold(topBar = { topBar() }) { innerPaddings ->
			Box(Modifier.padding(innerPaddings)) {
				LoadingContainer(loading = isLoading) {
					Column(
						modifier =
						Modifier.verticalScroll(rememberScrollState())
							.fillMaxWidth()
							.bodyPaddings(horizontal = 0f, vertical = 1.5f)
					) {
						channelInfo()
						Divider(modifier = Modifier.padding(top = 24.dp, bottom = 10.dp))

						TextIconListItem(
							icon = R.drawable.ic_notifications,
							text = stringResource(R.string.settings_sound_and_notifications),
							onClick = { coroutineScope.launch { bottomState.show() } }
						)
						Divider(modifier = Modifier.padding(top = 10.dp, bottom = 24.dp))

						channelMedia()
						Divider(modifier = Modifier.padding(top = 24.dp, bottom = 24.dp))

						channelMembers()
						Divider(modifier = Modifier.padding(top = 24.dp, bottom = 10.dp))

						if (!isOneToOne && channel !is GroupChannel) {
							TextIconListItem(
								icon = R.drawable.ic_exit,
								text = stringResource(R.string.leave_channel),
								color = AppTheme.colors.error,
								onClick = leaveChannel
							)
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun ChannelDetailsScreenPreview() = Preview {
	ChannelDetailsScreen(
		channelResult = Result.Success(FakeModel.GroupChannel()),
		chatMedia = FakeModel.media(),
		updateAlerts = {},
		leaveChannel = {},
		onEditClick = {},
		onAddMember = {},
		onMediaClick = {},
		onBackClick = {}
	)
}
