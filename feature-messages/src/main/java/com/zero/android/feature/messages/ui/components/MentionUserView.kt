package com.zero.android.feature.messages.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zero.android.common.R
import com.zero.android.models.Member
import com.zero.android.ui.components.SmallCircularImage
import com.zero.android.ui.theme.AppTheme

@Composable
fun MentionUsersList(membersList: List<Member>, onMemberSelected: (Member) -> Unit) {
	LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp)) {
		items(membersList) { member ->
			Column(modifier = Modifier.clickable { onMemberSelected(member) }) {
				Row(
					modifier = Modifier.fillMaxWidth().padding(12.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					SmallCircularImage(
						imageUrl = member.profileImage,
						placeHolder = R.drawable.ic_user_profile_placeholder
					)
					Spacer(modifier = Modifier.size(8.dp))
					Text(
						text = member.name ?: "",
						color = AppTheme.colors.colorTextPrimary,
						style = MaterialTheme.typography.bodyLarge,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				}
				Divider()
			}
		}
	}
}
