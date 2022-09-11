package com.zero.android.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun LifecycleOwner.onEvent(observer: LifecycleEventObserver) {
	DisposableEffect(this) {
		lifecycle.addObserver(observer)
		onDispose { lifecycle.removeObserver(observer) }
	}
}

@Composable
inline fun OnLifecycleEvent(observer: LifecycleEventObserver) {
	LocalLifecycleOwner.current.onEvent(observer)
}
