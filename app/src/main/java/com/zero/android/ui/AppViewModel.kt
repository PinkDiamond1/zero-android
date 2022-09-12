package com.zero.android.ui

import com.zero.android.common.extensions.emitInScope
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.common.usecases.ThemePaletteUseCase
import com.zero.android.data.manager.AuthManager
import com.zero.android.data.manager.ConnectionManager
import com.zero.android.datastore.AppPreferences
import com.zero.android.models.AuthCredentials
import com.zero.android.navigation.AppGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
	private val authManager: AuthManager,
	private val connectionManager: ConnectionManager,
	private val themePaletteUseCase: ThemePaletteUseCase
) : BaseViewModel() {

	val loading = MutableStateFlow(true)
	lateinit var startDestination: String

	val dynamicThemePalette: StateFlow<Int> = themePaletteUseCase.dynamicThemePalette

	init {
		checkAuthOnLaunch()
	}

	private fun checkAuthOnLaunch() {
		val authCredentials = runBlocking(Dispatchers.IO) { preferences.authCredentials() }
		val isLoggedIn = authCredentials != null
		startDestination = if (isLoggedIn) AppGraph.MAIN else AppGraph.AUTH

		if (isLoggedIn) onLoggedIn(authCredentials!!) else loading.emitInScope(false)
	}

	private fun onLoggedIn(authCredentials: AuthCredentials) =
		CoroutineScope(Dispatchers.IO).launch {
			authManager.onLogin(authCredentials)
			loading.emit(false)
		}

	override fun onCleared() {
		runBlocking { connectionManager.clear() }
		super.onCleared()
	}
}
