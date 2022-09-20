package com.zero.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.view.WindowCompat
import com.zero.android.ui.splash.SplashRoute
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		WindowCompat.setDecorFitsSystemWindows(window, false)
		setContent {
			SplashRoute(
				onNavigateAway = {
					startActivity(Intent(this, MainActivity::class.java))
					finish()
				}
			)
		}
	}
}
