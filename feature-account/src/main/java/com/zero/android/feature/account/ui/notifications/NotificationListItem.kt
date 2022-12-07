package com.zero.android.feature.account.ui.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.util.DateUtil
import com.zero.android.models.Notification
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.BODY_PADDING_HORIZONTAL
import com.zero.android.ui.util.LIST_IMAGE_LARGE
import com.zero.android.ui.util.Preview

@Composable
fun NotificationListItem(
	notification: Notification,
	endView: @Composable (() -> Unit)? = null,
	onClick: (Notification) -> Unit
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier =
		Modifier.fillMaxWidth()
			.clickable { onClick(notification) }
			.padding(horizontal = BODY_PADDING_HORIZONTAL.dp, vertical = 8.dp)
	) {
		Row {
			Box(modifier = Modifier.padding(0.dp)) {
				CircularInitialsImage(
					name = notification.title ?: "",
					url = notification.image,
					size = LIST_IMAGE_LARGE.dp
				)
			}
			Column(modifier = Modifier.padding(start = 12.dp)) {
				if (!notification.title.isNullOrEmpty()) {
					Text(
						text = notification.title!!,
						color = AppTheme.colors.colorTextPrimary,
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.Medium,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis
					)
				}
				Text(
					text = notification.description,
					color =
					if (notification.title.isNullOrEmpty()) AppTheme.colors.colorTextPrimary
					else AppTheme.colors.colorTextSecondary,
					style = MaterialTheme.typography.bodyLarge,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis
				)
				Text(
					text = DateUtil.getTimeAgoString(notification.createdAt.toEpochMilliseconds()),
					color = AppTheme.colors.colorTextSecondaryVariant,
					style = MaterialTheme.typography.bodySmall,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}
		}

		endView?.invoke()
	}
}

@Preview
@Composable
private fun NotificationsItemPreview() = Preview {
	NotificationListItem(FakeModel.Notification()) {}
}
