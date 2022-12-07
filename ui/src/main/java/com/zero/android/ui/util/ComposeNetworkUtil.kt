package com.zero.android.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.zero.android.common.system.NetworkManager
import com.zero.android.common.util.ConnectionState
import com.zero.android.common.util.connectionState

@Composable
fun connectivityState(): State<ConnectionState> {
	val context = LocalContext.current

	return produceState(initialValue = context.connectionState) {
		NetworkManager.observeConnection(context).collect { value = it }
	}
}

@Composable
inline fun OnConnectionChanged(crossinline onToggled: (connected: Boolean) -> Unit) {
	val connection by connectivityState()

	LaunchedEffect(connection) { onToggled(connection is ConnectionState.Available) }
}
