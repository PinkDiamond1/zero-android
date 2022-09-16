package com.zero.android.feature.people.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.Member
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.MediumCircularImage
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme

@Composable
fun MemberCircleItem(
	member: Member,
	topIcon: @Composable ((Modifier) -> Unit)? = null,
	onClick: (() -> Unit)? = null
) {
	val iconSize = 20.dp

	Box(modifier = Modifier.padding((iconSize / 2))) {
		MediumCircularImage(
			modifier = Modifier.clickable { onClick?.invoke() },
			placeHolder = R.drawable.ic_user_profile_placeholder,
			imageUrl = member.profileImage,
			contentDescription = member.id
		)

		if (topIcon != null) {
			val offsetInPx = LocalDensity.current.run { (iconSize / 2f).roundToPx() }

			topIcon(
				Modifier.size(iconSize)
					.offset { IntOffset(x = +offsetInPx, y = -offsetInPx) }
					.align(Alignment.TopEnd)
			)
		}
	}
}

@Preview
@Composable
fun MemberCircleItemPreview() = Preview {
	MemberCircleItem(
		member = FakeModel.Member(),
		topIcon = {
			IconButton(onClick = {}) {
				Icon(
					painter = painterResource(R.drawable.ic_cancel_24),
					contentDescription = "",
					tint = AppTheme.colors.surfaceVariant
				)
			}
		}
	)
}
