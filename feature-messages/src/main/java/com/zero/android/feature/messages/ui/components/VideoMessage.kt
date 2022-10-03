package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.zero.android.common.R
import com.zero.android.common.extensions.format
import com.zero.android.common.extensions.toDate
import com.zero.android.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers

private val frameDecoder = VideoFrameDecoder.Factory()
private val decoderDispatcher = Dispatchers.IO

@Composable
fun VideoMessage(fileUrl: String, messageDate: Long) {
	val date = messageDate.toDate()
	val model =
		ImageRequest.Builder(LocalContext.current)
			.decoderFactory(frameDecoder)
			.decoderDispatcher(decoderDispatcher)
			.crossfade(true)
			.data(fileUrl)
			.build()
	Column {
		Row {
			Spacer(modifier = Modifier.width(12.dp))
			Box(
				modifier =
				Modifier.size(200.dp)
					.clip(RoundedCornerShape(12.dp))
					.background(Color.Black, RoundedCornerShape(12.dp))
			) {
				AsyncImage(
					model,
					contentDescription = "",
					modifier = Modifier.fillMaxSize().padding(6.dp).align(Alignment.Center)
				)
				Box(
					modifier =
					Modifier.size(34.dp)
						.background(color = Color.Black.copy(0.5f), CircleShape)
						.align(Alignment.Center)
				) {
					Icon(
						modifier = Modifier.size(32.dp).align(Alignment.Center),
						painter = painterResource(R.drawable.ic_play_circle_24),
						contentDescription = null
					)
				}
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
