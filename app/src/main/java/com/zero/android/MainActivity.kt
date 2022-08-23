package com.zero.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zero.android.ui.AppLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private lateinit var navController: NavHostController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		installSplashScreen().run { setKeepOnScreenCondition { false } }
		setContent { AppLayout(controller = rememberNavController().also { navController = it }) }
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		navController.handleDeepLink(intent)
	}
}
