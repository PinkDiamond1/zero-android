package com.zero.android.common.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.zero.android.common.extensions.withSameScope
import com.zero.android.common.util.ConnectionState
import com.zero.android.common.util.connectivityState
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow

object NetworkManager {

	private const val CONNECTED_DELAY = 200L

	private fun networkCallback(
		callback: (ConnectionState) -> Unit
	): ConnectivityManager.NetworkCallback {
		return object : ConnectivityManager.NetworkCallback() {
			override fun onAvailable(network: Network) = callback(ConnectionState.Available)
			override fun onLost(network: Network) = callback(ConnectionState.Unavailable)
		}
	}

	fun observeConnection(context: Context, currentState: Boolean = false) = callbackFlow {
		val callback = networkCallback { withSameScope { notify(it) } }

		val connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkRequest =
			NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
		connectivityManager.registerNetworkCallback(networkRequest, callback)

		if (currentState) notify(connectivityManager.connectivityState())

		awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
	}

	private suspend fun ProducerScope<ConnectionState>.notify(state: ConnectionState) {
		if (state is ConnectionState.Available) delay(CONNECTED_DELAY)
		trySend(state)
	}
}
