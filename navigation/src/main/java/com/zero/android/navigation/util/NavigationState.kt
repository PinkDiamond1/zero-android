package com.zero.android.navigation.util

sealed interface NavigationState<out T> {
	data class Navigate<T>(val data: T) : NavigationState<T>
	object GoBack : NavigationState<Nothing>
	object Blank : NavigationState<Nothing>
}
