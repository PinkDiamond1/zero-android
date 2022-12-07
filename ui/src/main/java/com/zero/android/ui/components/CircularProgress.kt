package com.zero.android.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgress(modifier: Modifier = Modifier, size: Dp = 42.dp) =
	CircularProgressIndicator(modifier = modifier.size(size), strokeWidth = 5.dp)
