package com.zero.android.feature.people.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.Member
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.SmallCircularImage
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme

@Composable
fun MemberSearchItem(member: Member, onClick: (Member) -> Unit) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.fillMaxWidth().clickable { onClick(member) }
	) {
		SmallCircularImage(
			placeHolder = R.drawable.ic_user_profile_placeholder,
			imageUrl = member.profileImage,
			contentDescription = member.id
		)
		Spacer(modifier = Modifier.size(8.dp))
		Text(
			text = member.name ?: "",
			color = AppTheme.colors.colorTextPrimary,
			style = MaterialTheme.typography.bodyMedium,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)
	}
}

@Preview
@Composable
fun MemberSearchItemPreview() = Preview { MemberSearchItem(member = FakeModel.Member()) {} }
