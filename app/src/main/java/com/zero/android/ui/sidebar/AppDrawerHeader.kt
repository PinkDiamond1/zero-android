package com.zero.android.ui.sidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zero.android.common.R
import com.zero.android.models.Network
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.components.SmallClickableIcon
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme

@Composable
fun AppDrawerHeader(
	modifier: Modifier = Modifier,
	network: Network,
	onSettingsClick: () -> Unit,
	onInviteClick: () -> Unit
) {
	Column(modifier = modifier.fillMaxWidth()) {
		ConstraintLayout(
			modifier = modifier.fillMaxWidth().wrapContentHeight().padding(DRAWER_PADDING.dp)
		) {
			val (imageStart, textTop, textBottom, imageEnd, inviteButton) = createRefs()

			CircularInitialsImage(
				modifier =
				Modifier.constrainAs(imageStart) {
					top.linkTo(parent.top)
					start.linkTo(parent.start)
				},
				size = 42.dp,
				name = network.name,
				url = network.logo,
				placeholder = painterResource(R.drawable.ic_circular_image_placeholder)
			)

			Text(
				text = network.displayName,
				modifier =
				Modifier.constrainAs(textTop) {
					top.linkTo(imageStart.top)
					bottom.linkTo(textBottom.top)
					linkTo(start = imageStart.end, end = imageEnd.start, bias = 0.05f)
				},
				color = AppTheme.colors.colorTextPrimary,
				style = MaterialTheme.typography.bodyLarge,
				fontSize = 20.sp
			)
			Text(
				text = network.name,
				modifier =
				Modifier.constrainAs(textBottom) {
					top.linkTo(textTop.bottom)
					start.linkTo(textTop.start)
					bottom.linkTo(imageStart.bottom)
				},
				color = AppTheme.colors.colorTextSecondaryVariant,
				style = MaterialTheme.typography.bodyMedium
			)
			SmallClickableIcon(
				modifier =
				Modifier.constrainAs(imageEnd) {
					top.linkTo(imageStart.top)
					bottom.linkTo(imageStart.bottom)
					end.linkTo(parent.end)
				},
				icon = R.drawable.ic_settings,
				onClick = onSettingsClick,
				contentDescription = stringResource(R.string.cd_ic_settings),
				includePadding = false
			)
          /*OutlinedButton(
              onClick = onInviteClick,
              modifier =
              Modifier.constrainAs(inviteButton) {
                  top.linkTo(textBottom.bottom, margin = 16.dp)
                  start.linkTo(parent.start)
              },
              border = BorderStroke(1.dp, AppTheme.colors.glow),
              shape = RoundedCornerShape(24.dp)
          ) {
              Text(
                  text = stringResource(R.string.invite_members),
                  style =
                  MaterialTheme.typography.displayLarge.copy(
                      shadow =
                      Shadow(
                          color = AppTheme.colors.glow,
                          offset = Offset(2f, 2f),
                          blurRadius = 50f
                      )
                  ),
                  color = AppTheme.colors.colorTextPrimary
              )
          }*/
		}

		Divider(color = AppTheme.colors.divider, modifier = modifier.fillMaxWidth(), thickness = 0.5.dp)
	}
}

@Preview
@Composable
fun AppDrawerHeaderPreview() = Preview {
	AppDrawerHeader(network = FakeModel.Network(), onSettingsClick = {}) {}
}
