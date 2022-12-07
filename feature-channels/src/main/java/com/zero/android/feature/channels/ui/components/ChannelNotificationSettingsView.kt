package com.zero.android.feature.channels.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.zero.android.common.R
import com.zero.android.models.enums.AlertType
import com.zero.android.ui.components.TextIconListItem
import com.zero.android.ui.util.Preview

@Composable
fun ChannelNotificationSettingsView(
	modifier: Modifier = Modifier,
	onItemSelected: (AlertType) -> Unit
) {
	Column(modifier = modifier) {
		TextIconListItem(
			text = stringResource(R.string.alert_type_all),
			icon = R.drawable.ic_notifications
		) {
			onItemSelected(AlertType.ALL)
		}
		TextIconListItem(
			text = stringResource(R.string.alert_type_mentions_only),
			icon = R.drawable.ic_notificatons_mentions
		) {
			onItemSelected(AlertType.MENTION_ONLY)
		}
		TextIconListItem(
			text = stringResource(R.string.alert_type_off),
			icon = R.drawable.ic_notifications_off
		) {
			onItemSelected(AlertType.OFF)
		}
	}
}

@Preview(showBackground = false)
@Composable
fun ChannelNotificationSettingsViewPreview() = Preview {
	ChannelNotificationSettingsView(onItemSelected = {})
}
