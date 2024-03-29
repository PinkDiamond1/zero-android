package com.zero.android.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zero.android.BuildConfig
import com.zero.android.common.R
import com.zero.android.common.ui.Result
import com.zero.android.feature.channels.navigation.ChannelsDestination
import com.zero.android.feature.channels.navigation.CreateDirectChannelDestination
import com.zero.android.feature.channels.navigation.DirectChannelsDestination
import com.zero.android.feature.channels.ui.components.ChannelNotificationSettingsView
import com.zero.android.models.Network
import com.zero.android.navigation.HomeNavHost
import com.zero.android.navigation.NavDestination
import com.zero.android.navigation.extensions.navigate
import com.zero.android.ui.appbar.AppBottomBar
import com.zero.android.ui.appbar.AppTopBar
import com.zero.android.ui.components.Background
import com.zero.android.ui.components.SmallClickableIcon
import com.zero.android.ui.components.TextIconListItem
import com.zero.android.ui.sidebar.NetworkDrawerContent
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
	navController: NavController,
	viewModel: HomeViewModel = hiltViewModel(),
	onLogout: (String?) -> Unit,
	navigateToRootDestination: (NavDestination) -> Unit
) {
	val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
	val currentScreen by viewModel.currentScreen.collectAsState()
	val currentNetwork: Network? by viewModel.selectedNetwork.collectAsState()
	val networks: Result<List<Network>> by viewModel.networks.collectAsState()

	val unreadDMs by viewModel.unreadDMsCount.collectAsState()

	isUserLoggedIn?.let {
		if (it) {
			HomeScreen(
				viewModel = viewModel,
				navController = navController,
				currentScreen = currentScreen,
				currentNetwork = currentNetwork,
				networks = networks,
				unreadDMs = unreadDMs,
				onNetworkSelected = {
					viewModel.switchTheme()
					viewModel.onNetworkSelected(it)
				},
				onTriggerSearch = { viewModel.triggerSearch(it) },
				onLogout = { onLogout(viewModel.inviteCode) },
				navigateToRootDestination = navigateToRootDestination
			)
		} else {
			onLogout(viewModel.inviteCode)
		}
	}
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
	unreadDMs: Int,
	onNetworkSelected: (Network) -> Unit,
	onTriggerSearch: (Boolean) -> Unit,
	onLogout: () -> Unit,
	navigateToRootDestination: (NavDestination) -> Unit
) {
	val bottomNavController = rememberNavController()

	val scaffoldState = rememberScaffoldState()
	val coroutineScope = rememberCoroutineScope()

	var showMenu by remember { mutableStateOf(false) }

	bottomNavController.addOnDestinationChangedListener { _, _, _ -> onTriggerSearch(false) }

	val actionItems: @Composable RowScope.() -> Unit = {
    /*ExtraSmallCircularImage(
        imageUrl = viewModel.loggedInUserImage,
        placeHolder = R.drawable.img_profile_avatar
    )
    Spacer(modifier = modifier.size(6.dp))*/
		if (currentScreen == ChannelsDestination || currentScreen == DirectChannelsDestination) {
			SmallClickableIcon(
				icon = R.drawable.ic_add_circle,
				onClick = { navigateToRootDestination(CreateDirectChannelDestination) },
				contentDescription = stringResource(R.string.create_direct_message)
			)
			SmallClickableIcon(
				icon = R.drawable.ic_search,
				onClick = { onTriggerSearch(true) },
				contentDescription = stringResource(R.string.search_channels)
			)
		} else {
      /*IconButton(
          modifier = Modifier.size(24.dp),
          onClick = { navigateToRootDestination(CreateDirectChannelDestination) }
      ) {
          Image(
              painter = painterResource(R.drawable.ic_add_circle),
              contentDescription = stringResource(R.string.create_direct_message),
              colorFilter = ColorFilter.tint(AppTheme.colors.surface)
          )
      }
      IconButton(onClick = { showMenu = !showMenu }) {
          Icon(Icons.Default.MoreVert, contentDescription = "", tint = AppTheme.colors.surface)
      }
      DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false },
          modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
      ) {
          DropdownMenuItem(
              text = {
                  Text(
                      text = stringResource(R.string.search),
                      style = MaterialTheme.typography.customTextStyle(LocalTextStyle.current),
                      color = AppTheme.colors.surface
                  )
              },
              leadingIcon = {
                  Image(
                      painter = painterResource(R.drawable.ic_search),
                      contentDescription = stringResource(R.string.search_channels),
                      colorFilter = ColorFilter.tint(AppTheme.colors.surface)
                  )
              },
              onClick = {
                  showMenu = false
                  onTriggerSearch(true)
              }
          )
      }*/
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
			unreadDMs = unreadDMs,
			onNavigateToHomeDestination = {
				if (currentScreen == it) return@AppBottomBar
				coroutineScope.launch {
					viewModel.currentScreen.emit(it)
					scaffoldState.drawerState.close()
				}
				bottomNavController.navigate(it) {
					popUpTo(navController.graph.startDestinationId) { saveState = true }
					launchSingleTop = true
					restoreState = true
				}
			}
		)
	}

	val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
	val selectedNetworkSetting by viewModel.selectedNetworkSetting.collectAsState()
	val context = LocalContext.current

	if (currentScreen != ChannelsDestination || scaffoldState.drawerState.isOpen) {
		BackHandler {
			if (bottomState.isVisible) {
				coroutineScope.launch { bottomState.hide() }
			} else if (scaffoldState.drawerState.isOpen) {
				coroutineScope.launch { scaffoldState.drawerState.close() }
			} else if (currentScreen != ChannelsDestination) {
				coroutineScope.launch { viewModel.currentScreen.emit(ChannelsDestination) }
				bottomNavController.navigate(ChannelsDestination)
			}
		}
	}

	ModalBottomSheetLayout(
		sheetState = bottomState,
		sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
		sheetContent = {
			if (selectedNetworkSetting != null) {
				ChannelNotificationSettingsView { alertType ->
					selectedNetworkSetting?.let {
						viewModel.updateNetworkNotificationSetting(it, alertType)
					}
					coroutineScope.launch { bottomState.hide() }
				}
			} else {
				Column(modifier = modifier) {
					Text(
						text = stringResource(id = R.string.settings),
						color = AppTheme.colors.colorTextPrimary,
						style = MaterialTheme.typography.bodyMedium,
						modifier =
						Modifier.align(Alignment.CenterHorizontally)
							.padding(top = 16.dp, bottom = 2.dp)
					)
					Text(
						text = "${BuildConfig.VERSION_NAME}${if (BuildConfig.DEBUG) " (debug)" else ""}",
						color = AppTheme.colors.colorTextPrimary,
						style = MaterialTheme.typography.labelSmall,
						modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
					)
					Divider(color = AppTheme.colors.divider)
					TextIconListItem(text = stringResource(R.string.logout)) {
						viewModel.logout(context = context, onLogout = onLogout)
					}
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
			}
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
