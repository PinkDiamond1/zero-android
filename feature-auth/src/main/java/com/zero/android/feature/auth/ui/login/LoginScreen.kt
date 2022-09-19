package com.zero.android.feature.auth.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.feature.auth.AuthViewModel
import com.zero.android.feature.auth.AuthViewModel.AuthScreenUIState
import com.zero.android.feature.auth.ui.components.AuthBackground
import com.zero.android.feature.auth.ui.components.AuthButton
import com.zero.android.feature.auth.ui.components.AuthInputField
import com.zero.android.feature.auth.ui.components.PasswordTextField
import com.zero.android.feature.auth.util.AuthUtil
import com.zero.android.ui.components.AppAlertDialog
import com.zero.android.ui.components.CircularImage
import com.zero.android.ui.components.StrikeLabel
import com.zero.android.ui.extensions.Preview
import com.zero.android.ui.theme.AppTheme

@Composable
fun LoginRoute(
	viewModel: AuthViewModel = hiltViewModel(),
	onLogin: () -> Unit,
	onForgotPassword: () -> Unit,
	onRegister: () -> Unit
) {
	val uiState: AuthScreenUIState by viewModel.uiState.collectAsState()
	val loginValidator: AuthUtil.LoginValidator by viewModel.loginValidator.collectAsState()
	val isLoading: Boolean by viewModel.loading.collectAsState()
	val requestError: String? by viewModel.error.collectAsState()
	val context = LocalContext.current

	DisposableEffect(Unit) { onDispose { viewModel.resetErrorState(resetValidations = true) } }

	if (uiState == AuthScreenUIState.LOGIN) {
		LaunchedEffect(Unit) { onLogin() }
	} else {
		LoginScreen(
			requestError,
			isLoading,
			loginValidator,
			onForgotPassword,
			onRegister,
			onLogin = { email, password -> viewModel.login(email?.trim(), password?.trim()) },
			onLoginWithGoogle = { viewModel.loginWithGoogle(context) },
			onLoginWithApple = { viewModel.loginWithApple(context) },
			onErrorShown = { viewModel.resetErrorState() }
		)
	}
}

@Composable
fun LoginScreen(
	requestError: String?,
	isLoading: Boolean,
	loginValidator: AuthUtil.LoginValidator,
	onForgotPassword: () -> Unit,
	onRegister: () -> Unit,
	onLogin: (String?, String?) -> Unit,
	onLoginWithGoogle: () -> Unit,
	onLoginWithApple: () -> Unit,
	onErrorShown: () -> Unit
) {
	val isInviteLink: Boolean = false

	var email: String? by remember { mutableStateOf(null) }
	var password: String? by remember { mutableStateOf(null) }
	val emailError = remember(loginValidator) { mutableStateOf(loginValidator.emailError) }
	val passwordError = remember(loginValidator) { mutableStateOf(loginValidator.passwordError) }

	AuthBackground(isLoading) {
		Box {
			if (!requestError.isNullOrEmpty()) {
				AppAlertDialog(requestError) { onErrorShown() }
			}
			Column(
				modifier = Modifier.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.SpaceBetween
			) {
				if (isInviteLink) {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Image(
							modifier = Modifier.size(100.dp, height = 50.dp),
							painter = painterResource(R.drawable.zero_logo),
							contentDescription = "cd_zero_logo",
							contentScale = ContentScale.Fit
						)
						CircularImage(
							modifier = Modifier.size(100.dp),
							placeHolder = R.drawable.ic_circular_image_placeholder
						)
						Spacer(modifier = Modifier.size(8.dp))
						Text(
							text =
							buildAnnotatedString {
								withStyle(SpanStyle(color = AppTheme.colors.colorTextPrimary)) {
									append("Eth Fish has invited you to ")
								}
								withStyle(SpanStyle(color = AppTheme.colors.glow)) { append("Wilder World") }
								withStyle(SpanStyle(color = AppTheme.colors.colorTextPrimary)) { append("!") }
							},
							style = MaterialTheme.typography.displayLarge
						)
					}
				} else {
					Image(
						modifier = Modifier.padding(top = 50.dp),
						painter = painterResource(R.drawable.zero_logo),
						contentDescription = "cd_zero_logo"
					)
				}
				Column(
					modifier = Modifier.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					AuthInputField(
						modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
						placeHolder = { Text(stringResource(R.string.email_id)) },
						focusedByDefault = true,
						error = emailError.value,
						onTextChanged = {
							email = it
							emailError.value = null
						},
						keyboardOptions =
						KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
					)
					Spacer(modifier = Modifier.size(16.dp))
					PasswordTextField(
						modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
						placeHolder = { Text(stringResource(R.string.password)) },
						error = passwordError.value,
						onTextChanged = {
							password = it
							passwordError.value = null
						}
					)
					Spacer(modifier = Modifier.size(16.dp))
					AuthButton(text = stringResource(R.string.login)) { onLogin(email, password) }
					Spacer(modifier = Modifier.size(8.dp))
					StrikeLabel(
						text = stringResource(R.string.or_continue_with),
						textStyle = MaterialTheme.typography.displayMedium,
						strikeColors = listOf(Color.Black, AppTheme.colors.glow),
						paddingHorizontal = 20.dp,
						strikeSize = 1.dp
					)
					Spacer(modifier = Modifier.size(24.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceEvenly
					) {
						OutlinedButton(
							onClick = onLoginWithApple,
							modifier = Modifier.height(56.dp).weight(1f).padding(start = 20.dp, end = 6.dp),
							shape = RoundedCornerShape(35.dp),
							border = BorderStroke(1.dp, AppTheme.colors.glow.copy(0.25f))
						) {
							Icon(
								painter = painterResource(R.drawable.ic_apple),
								contentDescription = null,
								tint = AppTheme.colors.colorTextSecondary
							)
						}
						OutlinedButton(
							onClick = onLoginWithGoogle,
							modifier = Modifier.height(56.dp).weight(1f).padding(start = 6.dp, end = 20.dp),
							shape = RoundedCornerShape(35.dp),
							border = BorderStroke(1.dp, AppTheme.colors.glow.copy(0.25f))
						) {
							Icon(
								painter = painterResource(R.drawable.ic_google),
								contentDescription = null,
								tint = AppTheme.colors.colorTextSecondary
							)
						}
					}
				}
				Column(
					modifier = Modifier.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
          /*Text(
              modifier = Modifier.clickable { onRegister() },
              text = buildAnnotatedString {
                  withStyle(SpanStyle(color = AppTheme.colors.colorTextPrimary)) {
                      append(stringResource(R.string.not_a_member))
                  }
                  append(" ")
                  withStyle(style = SpanStyle(
                      color = AppTheme.colors.glow,
                      textDecoration = TextDecoration.Underline,
                  )) { append(stringResource(R.string.create_an_account)) }
              },
              style = MaterialTheme.typography.displayMedium
          )
          Spacer(modifier = Modifier.size(10.dp))*/
					Text(
						modifier = Modifier.clickable { onForgotPassword() },
						text = stringResource(R.string.forgot_password),
						color = AppTheme.colors.colorTextSecondaryVariant,
						textDecoration = TextDecoration.Underline,
						style = MaterialTheme.typography.displayMedium
					)
					Spacer(modifier = Modifier.size(20.dp))
				}
			}
		}
	}
}

@Preview @Composable
fun LoginScreenPreview() = Preview {}
