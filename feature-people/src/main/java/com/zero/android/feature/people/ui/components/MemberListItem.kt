package com.zero.android.feature.people.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.common.util.DateUtil
import com.zero.android.models.Member
import com.zero.android.models.enums.ConnectionStatus
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.SmallCircularImage
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.BODY_PADDING_HORIZONTAL
import com.zero.android.ui.theme.EmeraldGreen
import com.zero.android.ui.theme.Gray

@Composable
fun MemberListItem(
	member: Member,
	showStatus: Boolean = false,
	endView: @Composable (() -> Unit)? = null,
	onClick: (Member) -> Unit
) {
	val mIconSize = 16

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier =
		Modifier.fillMaxWidth()
			.clickable { onClick(member) }
			.padding(
				horizontal =
				(BODY_PADDING_HORIZONTAL - (if (showStatus) (mIconSize / 2) else 0)).dp,
				vertical = 8.dp
			)
	) {
		val iconSize = mIconSize.dp

		Row(verticalAlignment = Alignment.CenterVertically) {
			Box(modifier = Modifier.padding(if (showStatus) (iconSize / 2) else 0.dp)) {
				SmallCircularImage(
					placeholder = R.drawable.ic_user_profile_placeholder,
					imageUrl = member.profileImage,
					contentDescription = member.id
				)

				if (showStatus) {
					val offsetInPx = LocalDensity.current.run { (iconSize / 2f).roundToPx() }
					val color = if (member.status == ConnectionStatus.ONLINE) EmeraldGreen else Gray
					Box(
						modifier =
						Modifier.size(iconSize)
							.offset { IntOffset(x = offsetInPx, y = +offsetInPx / 2) }
							.align(Alignment.BottomEnd)
							.background(color, shape = RoundedCornerShape(iconSize))
					)
				}
			}
			Column(modifier = Modifier.padding(start = 8.dp)) {
				Text(
					text = member.name ?: "",
					color = AppTheme.colors.colorTextPrimary,
					style = MaterialTheme.typography.bodyMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				if (showStatus) {
					Text(
						text =
						if (member.status == ConnectionStatus.ONLINE) {
							stringResource(R.string.connection_status_online)
						} else {
							stringResource(
								R.string.last_seen_at,
								DateUtil.getTimeAgoString(member.lastSeenAt)
							)
						},
						color = AppTheme.colors.colorTextPrimary,
						style = MaterialTheme.typography.bodySmall,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				}
			}
		}

		endView?.let { it.invoke() }
	}
}

@Preview
@Composable
private fun MemberListItemPreview() = Preview {
	MemberListItem(member = FakeModel.Member(), showStatus = true) {}
}
