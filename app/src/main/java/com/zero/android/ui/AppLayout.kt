package com.zero.android.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zero.android.navigation.AppNavHost
import com.zero.android.ui.components.LoadingContainer
import com.zero.android.ui.theme.ZeroTheme

@Composable
fun AppLayout(
	modifier: Modifier = Modifier,
	viewModel: AppViewModel = hiltViewModel(),
	controller: NavHostController = rememberNavController()
) {
	ZeroTheme {
		val isLoading: Boolean by viewModel.loading.collectAsState()

		LoadingContainer(loading = isLoading, modifier = modifier.fillMaxSize()) {
			AppNavHost(
				navController = controller,
				modifier = modifier.systemBarsPadding(),
				startDestination = viewModel.startDestination
			)
		}
	}
}
