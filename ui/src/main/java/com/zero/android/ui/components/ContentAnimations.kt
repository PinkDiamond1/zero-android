package com.zero.android.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FadeAnimation(
	modifier: Modifier = Modifier,
	visible: Boolean = false,
	content: @Composable () -> Unit
) {
	AnimatedVisibility(modifier = modifier, visible = visible, enter = fadeIn(), exit = fadeOut()) {
		content()
	}
}

@Composable
fun FadeExpandAnimation(
	modifier: Modifier = Modifier,
	visible: Boolean = false,
	content: @Composable () -> Unit
) {
	AnimatedVisibility(
		modifier = modifier,
		visible = visible,
		enter = expandVertically() + fadeIn(),
		exit = shrinkVertically() + fadeOut()
	) { content() }
}

@Composable
fun FadeSlideAnimation(
	modifier: Modifier = Modifier,
	visible: Boolean = false,
	content: @Composable () -> Unit
) {
	AnimatedVisibility(
		modifier = modifier,
		visible = visible,
		enter = slideInHorizontally() + fadeIn(),
		exit = slideOutHorizontally() + fadeOut()
	) { content() }
}

@Composable
fun InstantAnimation(
	modifier: Modifier = Modifier,
	visible: Boolean = false,
	enterAnimation: EnterTransition = fadeIn(),
	exitAnimation: ExitTransition = fadeOut(),
	content: @Composable () -> Unit
) {
	AnimatedVisibility(
		modifier = modifier,
		visibleState = MutableTransitionState(visible).apply { targetState = !visible },
		enter = enterAnimation,
		exit = exitAnimation
	) { content() }
}
