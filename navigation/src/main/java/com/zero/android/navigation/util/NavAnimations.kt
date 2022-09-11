package com.zero.android.navigation.util

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

interface NavAnimation {
	val enterTransition: EnterTransition
	val exitTransition: ExitTransition
	val popEnterTransition: EnterTransition
	val popExitTransition: ExitTransition
}

object SlideNavAnimation : NavAnimation {
	override val enterTransition =
		slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(350))
	override val exitTransition =
		slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(350))
	override val popEnterTransition =
		slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(350))
	override val popExitTransition =
		slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(350))
}

object FadeNavAnimation : NavAnimation {
	override val enterTransition = fadeIn(animationSpec = tween(350), initialAlpha = 0f)
	override val exitTransition = fadeOut(animationSpec = tween(350), targetAlpha = 0f)
	override val popEnterTransition = fadeIn(animationSpec = tween(350), initialAlpha = 0f)
	override val popExitTransition = fadeOut(animationSpec = tween(350), targetAlpha = 0f)
}
