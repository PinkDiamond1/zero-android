package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.Channel
import com.zero.android.models.GroupChannel
import com.zero.android.models.fake.FakeModel
import com.zero.android.models.isGroupChannel
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.extensions.Preview

@Composable
fun ChatScreenAppBarTitle(channel: Channel) {
	Row {
		IconButton(modifier = Modifier.align(Alignment.CenterVertically), onClick = {}) {
			CircularInitialsImage(
				size = 36.dp,
				name = channel.name,
				url = channel.image,
				placeholder =
				if (channel.isGroupChannel) null
				else painterResource(R.drawable.ic_user_profile_placeholder)
			)
		}
		Text(
			channel.name.lowercase(),
			modifier = Modifier.align(Alignment.CenterVertically),
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			style = MaterialTheme.typography.displayLarge
		)
		Spacer(modifier = Modifier.padding(6.dp))
		if (channel.isGroupChannel) {
			(channel as GroupChannel).icon?.let { icon ->
				Image(
					painter = painterResource(icon),
					contentDescription = "",
					modifier = Modifier.wrapContentSize().align(Alignment.CenterVertically),
					contentScale = ContentScale.Fit
				)
				Spacer(modifier = Modifier.padding(6.dp))
			}
		}
	}
}

@Preview
@Composable
private fun ChatScreenAppBarTitlePreview() = Preview {
	ChatScreenAppBarTitle(channel = FakeModel.GroupChannel())
}
