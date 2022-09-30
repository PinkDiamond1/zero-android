package com.zero.android.ui.sidebar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.Network
import com.zero.android.models.enums.AlertType
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.CountBadge
import com.zero.android.ui.components.MediumCircularImage
import com.zero.android.ui.components.NameInitialsView
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme

@Composable
fun NetworkDrawerItem(
	modifier: Modifier = Modifier,
	item: Network,
	onItemClick: () -> Unit,
	onSettingsClick: () -> Unit
) {
	Row(
		modifier =
		modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp).clickable {
			onItemClick()
		},
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		if (item.logo.isNullOrEmpty()) {
			NameInitialsView(modifier = Modifier.size(42.dp), displayName = item.displayName)
		} else {
			MediumCircularImage(
				placeHolder = R.drawable.ic_circular_image_placeholder,
				imageUrl = item.logo,
				contentDescription = item.name
			)
		}
		Spacer(modifier = Modifier.size(10.dp))
		Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
			Text(
				text = item.displayName,
				color = AppTheme.colors.colorTextPrimary,
				style = MaterialTheme.typography.bodyLarge
			)
			Text(
				text = item.displayName,
				color = AppTheme.colors.colorTextSecondaryVariant,
				style = MaterialTheme.typography.bodyMedium
			)
		}
		Spacer(modifier = Modifier.size(10.dp))
		if (item.unreadCount > 0) {
			CountBadge(count = item.unreadCount)
			Spacer(modifier = Modifier.size(4.dp))
		}
		Image(
			painter =
			painterResource(
				when (item.alerts) {
					AlertType.DEFAULT,
					AlertType.ALL -> R.drawable.ic_notifications
					AlertType.MENTION_ONLY -> R.drawable.ic_notificatons_mentions
					AlertType.OFF -> R.drawable.ic_notifications_off
				}
			),
			contentDescription = stringResource(R.string.cd_ic_settings),
			contentScale = ContentScale.Fit,
			colorFilter = ColorFilter.tint(AppTheme.colors.surface),
			modifier = Modifier.wrapContentSize().clickable(onClick = onSettingsClick)
		)
	}
}

@Preview(showBackground = false)
@Composable
fun NetworkDrawerItemPreview() = Preview {
	NetworkDrawerItem(item = FakeModel.Network(), onItemClick = {}, onSettingsClick = {})
}
