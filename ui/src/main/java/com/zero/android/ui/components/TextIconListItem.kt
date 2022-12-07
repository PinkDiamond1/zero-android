package com.zero.android.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zero.android.common.R
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.Preview

@Composable
fun TextIconListItem(
	modifier: Modifier = Modifier,
	text: String,
	@DrawableRes icon: Int? = null,
	color: Color = AppTheme.colors.colorTextPrimary,
	onClick: () -> Unit
) {
	ConstraintLayout(modifier = modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp)) {
		val (iconRef, textRef) = createRefs()

		icon?.let {
			Image(
				painter = painterResource(icon),
				contentDescription = stringResource(R.string.cd_ic_settings),
				contentScale = ContentScale.Fit,
				colorFilter = ColorFilter.tint(color),
				modifier =
				modifier
					.size(24.dp)
					.constrainAs(iconRef) {
						top.linkTo(parent.top)
						bottom.linkTo(parent.bottom)
						start.linkTo(parent.start)
					}
					.padding(end = 8.dp)
			)
		}
		Text(
			text = text,
			modifier =
			modifier.constrainAs(textRef) {
				top.linkTo(parent.top)
				bottom.linkTo(parent.bottom)
				start.linkTo(iconRef.end)
			},
			color = color,
			style = MaterialTheme.typography.bodyLarge
		)
	}
}

@Preview(showBackground = false)
@Composable
private fun TextIconListItemPreview() = Preview {
	TextIconListItem(icon = R.drawable.ic_settings, text = "Item Text", onClick = {})
}
