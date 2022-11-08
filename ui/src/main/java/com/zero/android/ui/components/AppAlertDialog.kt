package com.zero.android.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppAlertDialog(
	title: String = "Zero-Tech",
	message: String,
	onDismiss: () -> Unit = {},
	confirmButton: @Composable (() -> Unit),
	dismissButton: @Composable (() -> Unit)? = null
) {
	AlertDialog(
		onDismissRequest = { onDismiss() },
		confirmButton = confirmButton,
		dismissButton = dismissButton,
		title = { Text(title) },
		text = { Text(text = message) }
	)
}
