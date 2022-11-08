package com.zero.android.common.extensions

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.rightSwipeGesture(onGestureDetected: () -> Unit) =
	this.pointerInput(Unit) {
		detectHorizontalDragGestures { change, dragAmount ->
			change.consume()
			if (dragAmount > 0) {
				onGestureDetected()
			}
		}
	}
