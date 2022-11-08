package com.zero.android.common.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

sealed class ConnectionState {
	object Available : ConnectionState()
	object Unavailable : ConnectionState()
}

val Context.connectionState: ConnectionState
	get() {
		val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		return connectivityManager.connectivityState()
	}

internal fun ConnectivityManager.connectivityState(): ConnectionState {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		val network = activeNetwork // network is currently in a high power state for performing data
		// transmission.
		network ?: return ConnectionState.Available
		val actNetwork = getNetworkCapabilities(network) ?: return ConnectionState.Unavailable
		return when {
			actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionState.Available
			actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionState.Available
			else -> ConnectionState.Unavailable
		}
	} else {
		val connected =
			allNetworks.any { network ->
				getNetworkCapabilities(network)
					?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
					?: false
			}

		return if (connected) ConnectionState.Available else ConnectionState.Unavailable
	}
}
