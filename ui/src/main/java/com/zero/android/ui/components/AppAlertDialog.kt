package com.zero.android.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.zero.android.common.R

@Composable
fun AppAlertDialog(error: String) {
    var showDialog by remember(error) { mutableStateOf(true) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false })
                { Text(text = stringResource(R.string.ok)) }
            },
            title = { Text(text = stringResource(R.string.error)) },
            text = { Text(text = error) }
        )
    }
}
