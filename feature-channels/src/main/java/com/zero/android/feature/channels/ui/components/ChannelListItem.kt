package com.zero.android.feature.channels.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zero.android.common.R
import com.zero.android.common.extensions.toDate
import com.zero.android.common.extensions.toMessageDateFormat
import com.zero.android.common.util.messageFormatter
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import com.zero.android.models.enums.DeliveryStatus
import com.zero.android.models.enums.MessageType
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.components.UnreadCountText
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.Blue300
import com.zero.android.ui.util.LIST_IMAGE_LARGE
import com.zero.android.ui.util.Preview

@Composable
fun ChannelListItem(loggedInUserId: String? = null, channel: Channel, onClick: (Channel) -> Unit) {
	val isDirectChannel = channel is DirectChannel

	ConstraintLayout(
		modifier =
		Modifier.fillMaxWidth()
			.wrapContentHeight()
			.clickable { onClick(channel) }
			.padding(12.dp)
	) {
		val (image, textTop, textBottom, dateTime, unreadCount) = createRefs()

		val imageModifier =
			Modifier.constrainAs(image) {
				top.linkTo(parent.top)
				bottom.linkTo(parent.bottom)
				start.linkTo(parent.start)
				end.linkTo(textTop.start)
			}

		CircularInitialsImage(
			modifier = imageModifier,
			size = LIST_IMAGE_LARGE.dp,
			name = channel.name,
			url = if (isDirectChannel) channel.image else null,
			placeholder =
			if (isDirectChannel) painterResource(R.drawable.ic_user_profile_placeholder)
			else null
		)

		Row(
			modifier =
			Modifier.constrainAs(textTop) {
				top.linkTo(image.top)
				bottom.linkTo(textBottom.top)
				start.linkTo(image.end, margin = 12.dp)
				end.linkTo(dateTime.start, margin = 12.dp)
				width = Dimension.fillToConstraints
			}
		) {
			Text(
				text = channel.name,
				color = AppTheme.colors.colorTextPrimary,
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Medium,
				modifier = Modifier.align(Alignment.CenterVertically),
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
			if (!isDirectChannel) {
				(channel as GroupChannel).icon?.let { icon ->
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
		Text(
			text =
			getLastMessage(
				context = LocalContext.current,
				channel = channel,
				annotationColor = AppTheme.colors.colorTextPrimary
			),
			color = AppTheme.colors.colorTextSecondary,
			style = MaterialTheme.typography.bodyMedium,
			modifier =
			Modifier.constrainAs(textBottom) {
				top.linkTo(textTop.bottom, margin = 4.dp)
				start.linkTo(textTop.start)
				end.linkTo(dateTime.start, margin = 4.dp)
				width = Dimension.fillToConstraints
			},
			maxLines = 2,
			overflow = TextOverflow.Ellipsis
		)
		Text(
			text = channel.lastMessage?.createdAt?.toDate()?.toMessageDateFormat() ?: "",
			color = AppTheme.colors.colorTextPrimary,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			modifier =
			Modifier.constrainAs(dateTime) {
				top.linkTo(textTop.top)
				bottom.linkTo(textTop.bottom)
				end.linkTo(parent.end)
			}
		)
		if (channel is DirectChannel) {
			if (channel.lastMessage?.author?.id?.equals(loggedInUserId, true) == true) {
				val deliveryReceiptIcon =
					if (channel.lastMessage?.deliveryStatus == DeliveryStatus.SENT) {
						painterResource(R.drawable.ic_check)
					} else painterResource(R.drawable.ic_double_check)
				val iconColor =
					if (channel.lastMessage?.deliveryStatus == DeliveryStatus.READ) Blue300
					else AppTheme.colors.colorTextPrimary
				Icon(
					painter = deliveryReceiptIcon,
					contentDescription = "cd_message_status",
					modifier =
					Modifier.width(18.dp).constrainAs(unreadCount) {
						bottom.linkTo(image.bottom)
						end.linkTo(parent.end)
					},
					tint = iconColor
				)
			} else {
				if (channel.unreadMessageCount > 0) {
					UnreadCountText(
						modifier =
						Modifier.constrainAs(unreadCount) {
							bottom.linkTo(image.bottom)
							end.linkTo(parent.end)
						},
						text = channel.unreadMessageCount.toString()
					)
				}
			}
		} else {
			if (channel.unreadMessageCount > 0) {
				UnreadCountText(
					modifier =
					Modifier.constrainAs(unreadCount) {
						bottom.linkTo(image.bottom)
						end.linkTo(parent.end)
					},
					text = channel.unreadMessageCount.toString()
				)
			}
		}
	}
}

fun getLastMessage(context: Context, channel: Channel, annotationColor: Color): String {
	return when (channel.lastMessage?.type) {
		MessageType.AUDIO -> context.getString(R.string.voice_message)
		MessageType.IMAGE -> context.getString(R.string.image)
		MessageType.VIDEO -> context.getString(R.string.video)
		else -> {
			val styledMessage =
				(channel.lastMessage?.message ?: "").messageFormatter(annotationColor = annotationColor)
			val lastMessageAuthorName =
				if (channel.memberCount > 2) {
					channel.lastMessage?.author?.name?.let { "${it.trim()}: " } ?: ""
				} else ""
			"${lastMessageAuthorName}$styledMessage"
		}
	}
}

@Preview
@Composable
private fun ChannelListItemPreview() = Preview {
	ChannelListItem(loggedInUserId = "", channel = FakeModel.GroupChannel(), onClick = {})
}
