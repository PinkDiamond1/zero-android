package com.zero.android.ui.sidebar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zero.android.common.R
import com.zero.android.models.Network
import com.zero.android.models.enums.AlertType
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.CountBadge
import com.zero.android.ui.components.MediumCircularImage
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
		modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 12.dp, vertical = 6.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		ConstraintLayout(
			modifier =
			modifier
				.weight(1f)
				.wrapContentHeight()
				.padding(end = 4.dp)
				.clickable(onClick = onItemClick)
		) {
			val (image, textTop, textBottom, textEnd) = createRefs()

			MediumCircularImage(
				modifier =
				modifier
					.constrainAs(image) {
						top.linkTo(parent.top)
						bottom.linkTo(parent.bottom)
						start.linkTo(parent.start)
					}
					.padding(end = 8.dp),
				placeHolder = R.drawable.ic_circular_image_placeholder,
				imageUrl = item.logo,
				contentDescription = item.name
			)
			Text(
				text = item.displayName,
				modifier =
				modifier.constrainAs(textTop) {
					top.linkTo(parent.top)
					bottom.linkTo(textBottom.top)
					linkTo(start = image.end, end = textEnd.start, bias = 0f)
				},
				color = AppTheme.colors.colorTextPrimary,
				style = MaterialTheme.typography.bodyLarge
			)
			Text(
				text = item.displayName,
				modifier =
				modifier.constrainAs(textBottom) {
					top.linkTo(textTop.bottom)
					bottom.linkTo(parent.bottom)
					start.linkTo(textTop.start)
					end.linkTo(textTop.end)
					width = Dimension.fillToConstraints
				},
				color = AppTheme.colors.colorTextSecondaryVariant,
				style = MaterialTheme.typography.bodyMedium
			)
			if (item.unreadCount > 0) {
				CountBadge(
					count = item.unreadCount,
					modifier =
					modifier.constrainAs(textEnd) {
						top.linkTo(parent.top)
						bottom.linkTo(parent.bottom)
						end.linkTo(parent.end)
					}
				)
			}
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
