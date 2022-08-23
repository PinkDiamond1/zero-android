package com.zero.android.feature.channels.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.enums.AlertType
import com.zero.android.ui.components.dialog.DialogListItem
import com.zero.android.ui.extensions.Preview

@Composable
fun ChannelNotificationSettingsView(
	modifier: Modifier = Modifier,
	onItemSelected: (AlertType) -> Unit,
	onCancelled: () -> Unit
) {
	Column(modifier = modifier) {
		DialogListItem(
			text = stringResource(R.string.alert_type_all),
			icon = R.drawable.ic_notifications
		) {
			onItemSelected(AlertType.ALL)
		}
		DialogListItem(
			text = stringResource(R.string.alert_type_mentions_only),
			icon = R.drawable.ic_notificatons_mentions
		) { onItemSelected(AlertType.ALL) }
		DialogListItem(
			text = stringResource(R.string.alert_type_off),
			icon = R.drawable.ic_notifications_off
		) {
			onItemSelected(AlertType.ALL)
		}

		Box(modifier = modifier.padding(top = 16.dp).clickable(onClick = onCancelled)) {
			Text(
				modifier = Modifier.fillMaxWidth().padding(16.dp),
				text = stringResource(R.string.cancel),
				textAlign = TextAlign.Center
			)
		}
	}
}

@Preview(showBackground = false)
@Composable
fun ChannelNotificationSettingsViewPreview() = Preview {
	ChannelNotificationSettingsView(onItemSelected = {}, onCancelled = {})
}
