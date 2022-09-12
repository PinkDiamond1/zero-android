package com.zero.android.system

import android.content.Context
import com.zero.android.common.system.SoundManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
	SoundManager
