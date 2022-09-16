package com.zero.android.feature.people.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zero.android.common.R
import com.zero.android.models.Member
import com.zero.android.ui.components.MediumCircularImage

@Composable
fun MemberCircleItem(member: Member, showStatus: Boolean = false, onClick: (() -> Unit)? = null) {
	ConstraintLayout(modifier = Modifier.size(36.dp).clickable { onClick?.invoke() }) {
		val (imageRef, statusRef) = createRefs()

		MediumCircularImage(
			modifier = Modifier.constrainAs(imageRef) {},
			placeHolder = R.drawable.ic_user_profile_placeholder,
			imageUrl = member.profileImage,
			contentDescription = member.id
		)

		if (showStatus) {
			Image(
				painter = ColorPainter(if (member.isActive) Color.Green else Color.Gray),
				contentDescription = "status",
				contentScale = ContentScale.Crop,
				modifier = Modifier.size(20.dp).clip(CircleShape)
			)
		}
	}
}
