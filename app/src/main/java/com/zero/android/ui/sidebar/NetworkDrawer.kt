package com.zero.android.ui.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zero.android.common.R
import com.zero.android.common.ui.Result
import com.zero.android.models.Network
import com.zero.android.models.fake.FakeModel
import com.zero.android.navigation.NavDestination
import com.zero.android.ui.components.LoadingContainer
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.BODY_PADDING_HORIZONTAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val DRAWER_PADDING = BODY_PADDING_HORIZONTAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDrawerContent(
	modifier: Modifier = Modifier,
	currentNetwork: Network?,
	networks: Result<List<Network>>,
	drawerState: DrawerState,
	coroutineScope: CoroutineScope,
	onNetworkSelected: (Network) -> Unit,
	onSettingsClicked: () -> Unit,
	onNetworkSettingsClick: (Network) -> Unit,
	onNavigateToRootDestination: (NavDestination) -> Unit
) {
	LoadingContainer(modifier = modifier.fillMaxSize(), loading = networks is Result.Loading) {
		ConstraintLayout(
			modifier =
			modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
		) {
			val (header, worldsLabel, items, footer) = createRefs()

			AppDrawerHeader(
				modifier =
				modifier.constrainAs(header) {
					top.linkTo(parent.top)
					start.linkTo(parent.start)
				},
				network = currentNetwork!!,
				onSettingsClick = { onSettingsClicked() },
				onInviteClick = { coroutineScope.launch { drawerState.close() } }
			)

			Text(
				text = stringResource(R.string.my_worlds),
				modifier =
				modifier.fillMaxWidth().padding(DRAWER_PADDING.dp).constrainAs(worldsLabel) {
					top.linkTo(header.bottom)
				},
				style = MaterialTheme.typography.labelLarge,
				color = AppTheme.colors.colorTextPrimary
			)
			LazyColumn(
				modifier =
				modifier.fillMaxWidth().constrainAs(items) {
					linkTo(top = worldsLabel.bottom, bottom = footer.top, bias = 0f)
				},
				userScrollEnabled = true
			) {
				if (networks is Result.Success) {
					items(items = networks.data, key = { item -> item.id }) { network ->
						DrawerItem(
							item = network,
							onItemClick = {
								onNetworkSelected(network)
								coroutineScope.launch { drawerState.close() }
							},
							onSettingsClick = { onNetworkSettingsClick(network) }
						)
					}
				}
			}

			AppDrawerFooter(
				modifier =
				modifier.constrainAs(footer) {
					bottom.linkTo(parent.bottom)
					start.linkTo(parent.start)
				},
				onCreateWorldClick = { coroutineScope.launch { drawerState.close() } }
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NetworkDrawerContentPreview() = Preview {
	NetworkDrawerContent(
		currentNetwork = FakeModel.Network(),
		networks = Result.Success(FakeModel.networks()),
		drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
		coroutineScope = CoroutineScope(Dispatchers.Default),
		onNetworkSelected = {},
		onNavigateToRootDestination = {},
		onSettingsClicked = {},
		onNetworkSettingsClick = {}
	)
}
