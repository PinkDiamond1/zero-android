package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zero.android.common.extensions.format
import com.zero.android.common.extensions.toDate
import com.zero.android.ui.theme.AppTheme

@Composable
fun ImageMessage(imageUrl: String, messageDate: Long) {
	val date = messageDate.toDate()
	Column {
		Row {
			Spacer(modifier = Modifier.width(12.dp))
			Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))) {
				AsyncImage(
					model =
					ImageRequest.Builder(LocalContext.current)
						.data(imageUrl)
						.crossfade(true)
						.crossfade(500)
						.build(),
					contentDescription = "",
					modifier = Modifier.wrapContentWidth().heightIn(max = 300.dp).align(Alignment.Center)
				)
			}
		}
		Text(
			text = date.format("hh:mm aa"),
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.align(Alignment.End).padding(start = 4.dp, end = 4.dp, bottom = 2.dp),
			color = AppTheme.colors.colorTextSecondary
		)
	}
}
