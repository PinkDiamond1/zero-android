package com.zero.android.network

interface SocketProvider {

	fun startListening(listener: SocketListener)

	fun stopListening()
}
