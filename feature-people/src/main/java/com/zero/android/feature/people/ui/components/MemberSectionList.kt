package com.zero.android.feature.people.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zero.android.models.Member
import com.zero.android.ui.components.InstantAnimation
import com.zero.android.ui.theme.AppTheme

@Composable
fun MemberSectionList(members: List<Member>, onMemberSelected: (Member) -> Unit) {
	val categorisedMembers = members.groupBy { if (it.name.isNullOrEmpty()) "" else it.name }

	InstantAnimation(modifier = Modifier.fillMaxSize()) {
		Column(modifier = Modifier.fillMaxWidth()) {
			categorisedMembers.forEach { entry ->
				val header = entry.key ?: return@forEach
				val sectionMembers = entry.value

				Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							header,
							color = AppTheme.colors.colorTextPrimary,
							style = MaterialTheme.typography.bodyMedium
						)
					}
					Spacer(modifier = Modifier.size(8.dp))
					LazyColumn(modifier = Modifier.fillMaxWidth()) {
						items(sectionMembers) { MemberSearchItem(it, onMemberSelected) }
					}
				}
			}
		}
	}
}
