package com.zero.android.feature.channels.ui.members

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zero.android.common.R
import com.zero.android.feature.people.ui.components.MemberCircleItem
import com.zero.android.feature.people.ui.components.MemberSectionList
import com.zero.android.models.Member
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.components.InstantAnimation
import com.zero.android.ui.components.SearchView
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMembersScreen(
	ui: SelectMembersUI,
	searchText: String,
	members: List<Member>,
	selectedUsers: List<Member>,
	loading: Boolean,
	onBackClick: () -> Unit,
	onDone: () -> Unit,
	onMemberSelected: (Member) -> Unit,
	onMemberRemoved: (Member) -> Unit,
	onSearchTextChange: (String) -> Unit
) {
	val topBar: @Composable () -> Unit = {
		AppBar(
			navIcon = {
				IconButton(onClick = { if (!loading) onBackClick() }) {
					Icon(
						imageVector = Icons.Filled.ArrowBack,
						contentDescription = "cd_back",
						tint = AppTheme.colors.glow
					)
				}
			},
			title = { Text(text = ui.title, style = MaterialTheme.typography.displayLarge) },
			actions = {
				OutlinedButton(enabled = selectedUsers.isNotEmpty() || loading, onClick = onDone) {
					Text(text = ui.cta, color = AppTheme.colors.colorTextPrimary, fontSize = 12.sp)
				}
			}
		)
	}

	Scaffold(topBar = { topBar() }) { innerPaddings ->
		Box(Modifier.padding(innerPaddings)) {
			Column(modifier = Modifier.fillMaxWidth()) {
				SearchView(
					searchText = searchText,
					placeHolder = stringResource(R.string.search_users),
					showSearchCancel = false,
					onValueChanged = { onSearchTextChange(it) }
				)
				Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
					if (selectedUsers.isNotEmpty()) {
						SelectedMembers(
							modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
							members = selectedUsers,
							onMemberRemoved = onMemberRemoved
						)
					}
					MemberSectionList(members, nested = true, onMemberSelected)
				}
			}
		}
	}
}

@Composable
private fun SelectedMembers(
	modifier: Modifier = Modifier,
	members: List<Member>,
	onMemberRemoved: (Member) -> Unit
) {
	InstantAnimation(modifier = modifier) {
		LazyRow(modifier = modifier) {
			items(members) { member ->
				MemberCircleItem(
					member = member,
					topIcon = {
						IconButton(modifier = it, onClick = { onMemberRemoved(member) }) {
							Icon(
								painter = painterResource(R.drawable.ic_cancel_24),
								contentDescription = "",
								tint = AppTheme.colors.surface
							)
						}
					}
				)
			}
		}
	}
}

data class SelectMembersUI(val title: String, val cta: String)

@Preview
@Composable
private fun SelectMembersScreenPreview() = Preview {
	SelectMembersScreen(
		ui = SelectMembersUI(title = "Title", cta = "Done"),
		searchText = "search text",
		members = FakeModel.members(),
		selectedUsers = listOf(FakeModel.Member("one")),
		loading = false,
		onBackClick = {},
		onDone = {},
		onMemberSelected = {},
		onMemberRemoved = {},
		onSearchTextChange = {}
	)
}
