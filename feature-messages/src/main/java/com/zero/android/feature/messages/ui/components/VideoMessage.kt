package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zero.android.common.R

@Composable
fun VideoMessage(fileUrl: String) {
	Box(modifier = Modifier.size(200.dp).background(Color.Black, shape = RoundedCornerShape(12.dp))) {
		Icon(
			modifier = Modifier.align(Alignment.Center).size(32.dp),
			painter = painterResource(R.drawable.ic_play_circle_24),
			contentDescription = null
		)
	}
}
