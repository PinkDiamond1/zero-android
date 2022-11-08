package com.zero.android.ui

import com.zero.android.common.extensions.emitInScope
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.manager.ConnectionManager
import com.zero.android.data.manager.SessionManager
import com.zero.android.datastore.AppPreferences
import com.zero.android.models.AuthCredentials
import com.zero.android.navigation.AppGraph
import com.zero.android.ui.manager.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AppViewModel
@Inject
constructor(
	private val preferences: AppPreferences,
	private val sessionManager: SessionManager,
	private val connectionManager: ConnectionManager,
	themeManager: ThemeManager
) : BaseViewModel() {

	val loading = MutableStateFlow(true)
	lateinit var startDestination: String

	val dynamicThemePalette: StateFlow<Int> = themeManager.dynamicThemePalette

	init {
		checkAuthOnLaunch()
	}

	private fun checkAuthOnLaunch() {
		val authCredentials = runBlocking(Dispatchers.IO) { preferences.authCredentials() }
		val isLoggedIn = authCredentials != null
		startDestination = AppGraph.MAIN

		if (isLoggedIn) onLoggedIn(authCredentials!!) else loading.emitInScope(false)
	}

	private fun onLoggedIn(authCredentials: AuthCredentials) =
		ioScope.launch {
			sessionManager.onLogin(authCredentials)
			loading.emit(false)
		}

	override fun onCleared() {
		runBlocking { connectionManager.clear() }
		super.onCleared()
	}
}
