package com.zero.android.ui.util

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavAnimationUtil {
	val DEFAULT_ENTER_ANIM =
		slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(350))
	val DEFAULT_EXIT_ANIM =
		slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(350))
	val DEFAULT_POP_ENTER_ANIM =
		slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(350))
	val DEFAULT_POP_EXIT_ANIM =
		slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(350))
}
