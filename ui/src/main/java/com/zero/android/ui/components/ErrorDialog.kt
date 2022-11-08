package com.zero.android.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.zero.android.common.R

@Composable
fun ErrorDialog(error: String, onDismiss: () -> Unit = {}) {
	var showDialog by remember { mutableStateOf(true) }
	if (showDialog) {
		AlertDialog(
			onDismissRequest = {
				showDialog = false
				onDismiss()
			},
			confirmButton = {
				TextButton(
					onClick = {
						showDialog = false
						onDismiss()
					}
				) {
					Text(text = stringResource(R.string.ok))
				}
			},
			title = { Text(text = stringResource(R.string.error)) },
			text = { Text(text = error) }
		)
	}
}
