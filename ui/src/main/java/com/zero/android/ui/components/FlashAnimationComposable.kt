package com.zero.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.zero.android.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun FlashAnimationComposable(
	initialValue: Float = 0.15f,
	targetValue: Float = 0f,
	transitionDurationMillis: Int = 500,
	flashColor: Color = AppTheme.colors.surface,
	startAnimation: Boolean = false,
	onEndAnimation: () -> Unit,
	content: @Composable () -> Unit
) {
	val infiniteTransition = rememberInfiniteTransition()
	val scope = rememberCoroutineScope()

	val startTimer: () -> Unit = {
		scope.launch {
			delay(3000)
			onEndAnimation()
		}
	}

	val flashAnimation by
	infiniteTransition.animateFloat(
		initialValue = initialValue,
		targetValue = targetValue,
		animationSpec =
		infiniteRepeatable(
			tween(transitionDurationMillis, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		)
	)
	if (startAnimation) {
		Box(modifier = Modifier.fillMaxWidth().background(flashColor.copy(flashAnimation))) {
			content()
		}
		startTimer()
	} else {
		Box(modifier = Modifier.fillMaxWidth()) { content() }
	}
}
