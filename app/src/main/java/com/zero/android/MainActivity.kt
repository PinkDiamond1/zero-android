package com.zero.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.zero.android.ui.AppLayout
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private lateinit var navController: NavHostController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		installSplashScreen().run { setKeepOnScreenCondition { false } }
		setContent {
			AppLayout(controller = rememberAnimatedNavController().also { navController = it })
		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		navController.handleDeepLink(intent)
	}
}
