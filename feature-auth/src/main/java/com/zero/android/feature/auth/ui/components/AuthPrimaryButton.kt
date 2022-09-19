package com.zero.android.feature.auth.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.zero.android.common.R

@Composable
fun AuthButton(text: String, onClick: () -> Unit = {}) {
	Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
		Image(
			modifier = Modifier.fillMaxWidth().clickable { onClick() },
			painter = painterResource(R.drawable.primary_cta),
			contentDescription = null,
			contentScale = ContentScale.FillWidth
		)
		Text(
			text = text,
			style =
			MaterialTheme.typography.displayLarge.copy(
				shadow =
				Shadow(
					color = MaterialTheme.colorScheme.outline,
					offset = Offset(2f, 2f),
					blurRadius = 50f
				)
			),
			color = Color.White
		)
	}
}
