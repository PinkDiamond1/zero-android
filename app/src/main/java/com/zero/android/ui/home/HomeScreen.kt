package com.zero.android.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zero.android.common.R
import com.zero.android.common.ui.Result
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.feature.channels.navigation.CreateDirectChannelDestination
import com.zero.android.feature.channels.navigation.DirectChannelDestination
import com.zero.android.feature.channels.ui.components.ChannelNotificationSettingsView
import com.zero.android.models.Network
import com.zero.android.navigation.HomeNavHost
import com.zero.android.navigation.NavDestination
import com.zero.android.ui.appbar.AppBottomBar
import com.zero.android.ui.appbar.AppTopBar
import com.zero.android.ui.components.Background
import com.zero.android.ui.components.dialog.DialogListItem
import com.zero.android.ui.sidebar.NetworkDrawerContent
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
	navController: NavController,
	viewModel: HomeViewModel = hiltViewModel(),
	onLogout: () -> Unit,
	navigateToRootDestination: (NavDestination) -> Unit
) {
	val currentScreen by viewModel.currentScreen.collectAsState()
	val currentNetwork: Network? by viewModel.selectedNetwork.collectAsState()
	val networks: Result<List<Network>> by viewModel.networks.collectAsState()

	HomeScreen(
		viewModel = viewModel,
		navController = navController,
		currentScreen = currentScreen,
		currentNetwork = currentNetwork,
		networks = networks,
		onNetworkSelected = {
			viewModel.switchTheme()
			viewModel.onNetworkSelected(it)
		},
		onTriggerSearch = { viewModel.triggerSearch(it) },
		onLogout = onLogout,
		navigateToRootDestination = navigateToRootDestination
	)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
	modifier: Modifier = Modifier,
	viewModel: HomeViewModel,
	navController: NavController,
	currentScreen: NavDestination,
	currentNetwork: Network?,
	networks: Result<List<Network>>,
	onNetworkSelected: (Network) -> Unit,
	onTriggerSearch: (Boolean) -> Unit,
	onLogout: () -> Unit,
	navigateToRootDestination: (NavDestination) -> Unit
) {
	val bottomNavController = rememberNavController()

	val scaffoldState = rememberScaffoldState()
	val coroutineScope = rememberCoroutineScope()

	// 	var showMenu by remember { mutableStateOf(false) }

	bottomNavController.addOnDestinationChangedListener { _, _, _ -> onTriggerSearch(false) }

	val actionItems: @Composable RowScope.() -> Unit = {
		if (currentScreen == ChannelsDestination || currentScreen == DirectChannelDestination) {
			IconButton(onClick = { onTriggerSearch(true) }, modifier = Modifier.size(32.dp)) {
				Image(
					painter = painterResource(R.drawable.ic_search),
					contentDescription = stringResource(R.string.search_channels)
				)
			}
      /*IconButton(onClick = { showMenu = !showMenu }) {
          Icon(Icons.Default.MoreVert, contentDescription = "")
      }*/
			IconButton(onClick = {}) {
				Image(
					painter = painterResource(R.drawable.img_profile_avatar),
					contentDescription = stringResource(R.string.profile)
				)
			}
			if (currentScreen == DirectChannelDestination) {
				IconButton(
					modifier = Modifier.size(28.dp),
					onClick = { navigateToRootDestination(CreateDirectChannelDestination) }
				) {
					Image(
						painter = painterResource(R.drawable.ic_add_circle),
						contentDescription = stringResource(R.string.create_direct_message)
					)
				}
			}
      /*DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
          DropdownMenuItem(
              text = { Text(text = stringResource(R.string.profile), style = MaterialTheme.typography.customTextStyle(
                           LocalTextStyle.current
                       )) },
              onClick = {},
              leadingIcon = {
                  Image(
                      painter = painterResource(R.drawable.img_profile_avatar),
                      contentDescription = stringResource(R.string.profile)
                  )
              }
          )
          DropdownMenuItem(
              text = { Text(text = stringResource(R.string.create_a_world), style = MaterialTheme.typography.customTextStyle(
                           LocalTextStyle.current
                       )) },
              onClick = {},
              leadingIcon = {
                  IconButton(
                      onClick = {},
                      modifier = Modifier
                                   .border(1.dp, AppTheme.colors.glow, CircleShape)
                                   .size(32.dp)
                  ) {
                      Icon(
                          imageVector = Icons.Filled.Add,
                          contentDescription = stringResource(R.string.create_a_world)
                      )
                  }
              }
          )
      }*/
		} else {
			IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
				Image(
					painter = painterResource(R.drawable.img_profile_avatar),
					contentDescription = stringResource(R.string.profile)
				)
			}
			Spacer(modifier = Modifier.padding(4.dp))
			IconButton(
				onClick = {},
				modifier = Modifier.border(1.dp, AppTheme.colors.glow, CircleShape).size(32.dp)
			) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.create_a_world)
				)
			}
			Spacer(modifier = Modifier.padding(4.dp))
		}
	}

	val topBar: @Composable () -> Unit = {
		AppTopBar(
			network = currentNetwork,
			openDrawer = { coroutineScope.launch { scaffoldState.drawerState.open() } },
			actions = actionItems
		)
	}

	val bottomBar: @Composable () -> Unit = {
		AppBottomBar(
			currentDestination = currentScreen,
			onNavigateToHomeDestination = {
				coroutineScope.launch {
					viewModel.currentScreen.emit(it)
					scaffoldState.drawerState.close()
				}
				bottomNavController.navigate(it.route) {
					popUpTo(navController.graph.startDestinationId) { saveState = true }
					launchSingleTop = true
					restoreState = true
				}
			}
		)
	}

	if (scaffoldState.drawerState.isOpen) {
		BackHandler { coroutineScope.launch { scaffoldState.drawerState.close() } }
	}

	val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
	val selectedNetworkSetting by viewModel.selectedNetworkSetting.collectAsState()
	val context = LocalContext.current

	ModalBottomSheetLayout(
		sheetState = bottomState,
		sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
		sheetContent = {
			if (selectedNetworkSetting != null) {
				ChannelNotificationSettingsView(
					onItemSelected = { alertType ->
						selectedNetworkSetting?.let {
							viewModel.updateNetworkNotificationSetting(it, alertType)
						}
						coroutineScope.launch { bottomState.hide() }
					}
				)
			} else {
				DialogListItem(text = stringResource(R.string.logout)) {
					viewModel.logout(context = context, onLogout = onLogout)
				}
			}
		}
	) {
		Scaffold(
			topBar = { topBar() },
			bottomBar = { bottomBar() },
			scaffoldState = scaffoldState,
			drawerContent = {
				NetworkDrawerContent(
					modifier = modifier,
					currentNetwork = currentNetwork,
					networks = networks,
					drawerState =
					DrawerState(
						DrawerValue.valueOf(scaffoldState.drawerState.currentValue.toString())
					) {
						coroutineScope.launch {
							if (it == DrawerValue.Open) scaffoldState.drawerState.open()
							else scaffoldState.drawerState.close()
						}
						true
					},
					coroutineScope = coroutineScope,
					onNetworkSelected = {
						onNetworkSelected(it)
						coroutineScope.launch { scaffoldState.drawerState.close() }
					},
					onNavigateToRootDestination = navigateToRootDestination,
					onSettingsClicked = {
						viewModel.onNetworkSettingSelected(null)
						coroutineScope.launch { bottomState.show() }
					},
					onNetworkSettingsClick = {
						coroutineScope.launch {
							viewModel.onNetworkSettingSelected(it)
							bottomState.show()
						}
					}
				)
			},
			drawerGesturesEnabled = scaffoldState.drawerState.isOpen
		) { innerPadding ->
			Background {
				Box(modifier = Modifier.padding(innerPadding)) {
					HomeNavHost(
						bottomNavController = bottomNavController,
						navController = navController,
						network = currentNetwork
					)
				}
			}
		}
	}
}
