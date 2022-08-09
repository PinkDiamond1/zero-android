package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.Channel
import com.zero.android.models.GroupChannel
import com.zero.android.ui.components.NameInitialsView
import com.zero.android.ui.components.SmallCircularImage
import com.zero.android.ui.theme.customTextStyle

@Composable
fun ChatScreenAppBarTitle(channel: Channel, isGroupChannel: Boolean) {
	Row {
		IconButton(modifier = Modifier.align(Alignment.CenterVertically), onClick = {}) {
			if (isGroupChannel) {
				NameInitialsView(userName = channel.name)
			} else {
				SmallCircularImage(
					imageUrl = channel.members.firstOrNull()?.profileImage,
					placeHolder = R.drawable.ic_user_profile_placeholder
				)
			}
		}
		Text(
			channel.name.lowercase(),
			modifier = Modifier.align(Alignment.CenterVertically),
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			style = MaterialTheme.typography.customTextStyle(LocalTextStyle.current)
		)
		Spacer(modifier = Modifier.padding(6.dp))
		if (isGroupChannel) {
			if ((channel as GroupChannel).hasTelegramChannel) {
				Image(
					painter = painterResource(R.drawable.ic_vector),
					contentDescription = "",
					modifier = Modifier.wrapContentSize().align(Alignment.CenterVertically),
					contentScale = ContentScale.Fit
				)
				Spacer(modifier = Modifier.padding(6.dp))
			}
			if (channel.hasDiscordChannel) {
				Image(
					painter = painterResource(R.drawable.ic_discord),
					contentDescription = "",
					modifier = Modifier.wrapContentSize().align(Alignment.CenterVertically),
					contentScale = ContentScale.Fit
				)
			}
		}
	}
}
