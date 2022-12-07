package com.zero.android.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.ui.components.TextIconListItem
import com.zero.android.ui.util.Preview

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetLayout(
	state: ModalBottomSheetState,
	content: @Composable ColumnScope.() -> Unit,
	layout: @Composable () -> Unit
) {
	ModalBottomSheetLayout(
		sheetState = state,
		modifier = Modifier.shadow(shape = RoundedCornerShape(8.dp), elevation = 2.dp),
		sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
		sheetContentColor = MaterialTheme.colorScheme.surfaceVariant,
		sheetContent = content,
		content = layout
	)
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = false)
@Composable
fun BottomSheetLayoutPreview() = Preview {
	BottomSheetLayout(
		state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded),
		content = {
			Column { TextIconListItem(icon = R.drawable.ic_settings, text = "Item Text", onClick = {}) }
		}
	) {
		Text(text = "Testing")
	}
}
