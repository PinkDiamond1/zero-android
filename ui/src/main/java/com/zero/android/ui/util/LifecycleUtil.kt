package com.zero.android.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun OnLifecycleEvent(observer: LifecycleEventObserver) {
	val lifecycleOwner = LocalLifecycleOwner.current

	DisposableEffect(lifecycleOwner) {
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}
}

@Composable
fun OnResume(refresh: () -> Unit) {
	val rendered = remember { mutableStateOf(false) }

	OnLifecycleEvent { _, event ->
		if (event == Lifecycle.Event.ON_START) {
			if (rendered.value) refresh()
			rendered.value = true
		}
	}
}
