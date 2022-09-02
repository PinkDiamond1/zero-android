package com.zero.android.feature.auth.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.zero.android.common.R
import com.zero.android.ui.components.OverlappingLoadingContainer

@Composable
fun AuthBackground(isLoading: Boolean = false, content: @Composable () -> Unit) {
	Box {
		Image(
			modifier = Modifier.fillMaxWidth(),
			painter = painterResource(R.drawable.bg_auth),
			contentDescription = "auth_bg",
			contentScale = ContentScale.Crop
		)
		OverlappingLoadingContainer(
			loading = isLoading,
			modifier = Modifier.fillMaxSize().background(Color.Transparent)
		) {
			content()
		}
	}
}
