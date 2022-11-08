package com.zero.android.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.zero.android.common.util.ConnectionState
import com.zero.android.ui.util.connectivityState

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

@Composable
inline fun OnConnectionChanged(crossinline onToggled: (connected: Boolean) -> Unit) {
	val connection by connectivityState()

	LaunchedEffect(connection) { onToggled(connection is ConnectionState.Available) }
}
