package com.zero.android.feature.messages.ui.components

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import kotlinx.coroutines.launch

@Composable
fun VideoMessage(fileUrl: String) {
	val coroutineScope = rememberCoroutineScope()
	var videoImage by remember { mutableStateOf<Bitmap?>(null) }

	LaunchedEffect(Unit) {
		coroutineScope.launch {
			val mediaMetadataRetriever = MediaMetadataRetriever()
			mediaMetadataRetriever.setDataSource(fileUrl, hashMapOf())
			videoImage = mediaMetadataRetriever.getFrameAtTime(1000) // unit in microsecond
		}
	}

	Box(modifier = Modifier.size(200.dp).background(Color.Black, shape = RoundedCornerShape(12.dp))) {
		videoImage?.let {
			Image(
				modifier = Modifier.fillMaxSize().padding(6.dp).align(Alignment.Center),
				bitmap = it.asImageBitmap(),
				contentDescription = ""
			)
		}
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
