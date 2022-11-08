package com.zero.android.ui.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zero.android.ui.theme.BODY_PADDING_HORIZONTAL
import com.zero.android.ui.theme.BODY_PADDING_VERTICAL

fun Modifier.bodyPaddings(vertical: Float = 1f, horizontal: Float = 1f) =
	padding(
		vertical = (BODY_PADDING_VERTICAL * vertical).dp,
		horizontal = (BODY_PADDING_HORIZONTAL * horizontal).dp
	)

@Suppress("FunctionName")
fun BodyPaddingValues(vertical: Float = 1f, horizontal: Float = 1f) =
	PaddingValues(
		vertical = (BODY_PADDING_VERTICAL * vertical).dp,
		horizontal = (BODY_PADDING_HORIZONTAL * horizontal).dp
	)
