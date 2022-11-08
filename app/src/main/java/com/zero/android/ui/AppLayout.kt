package com.zero.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.zero.android.navigation.AppNavHost
import com.zero.android.ui.components.LoadingContainer
import com.zero.android.ui.theme.ZeroTheme

@ExperimentalAnimationApi
@Composable
fun AppLayout(
	modifier: Modifier = Modifier,
	viewModel: AppViewModel = hiltViewModel(),
	controller: NavHostController = rememberAnimatedNavController()
) {
	val dynamicThemePalette: Int by viewModel.dynamicThemePalette.collectAsState()

	ZeroTheme(randomThemePalette = dynamicThemePalette) {
		val isLoading by viewModel.loading.collectAsState()

		LoadingContainer(loading = isLoading) {
			AppNavHost(
				navController = controller,
				modifier = modifier.systemBarsPadding(),
				startDestination = viewModel.startDestination
			)
		}
	}
}
