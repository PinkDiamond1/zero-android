package com.zero.android.feature.auth.ui.resetpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.feature.auth.AuthViewModel
import com.zero.android.feature.auth.ui.components.AuthBackground
import com.zero.android.feature.auth.ui.components.AuthButton
import com.zero.android.feature.auth.ui.components.AuthInputField
import com.zero.android.feature.auth.util.AuthValidator
import com.zero.android.ui.components.AppAlertDialog
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.theme.customTextStyle
import com.zero.android.ui.util.BackHandler

@Composable
fun ResetPasswordRoute(viewModel: AuthViewModel = hiltViewModel(), onBack: () -> Unit) {
	val forgotPasswordValidator by viewModel.forgotPasswordValidator.collectAsState()
	val forgotPasswordRequestState by viewModel.forgotPasswordRequestState.collectAsState()
	val isLoading: Boolean by viewModel.loading.collectAsState()
	val requestError: String? by viewModel.error.collectAsState()

	DisposableEffect(Unit) { onDispose { viewModel.resetErrorState(resetValidations = true) } }

	BackHandler { onBack() }
	ResetPasswordScreen(
		isLoading,
		forgotPasswordValidator,
		forgotPasswordRequestState,
		requestError,
		onBack,
		onForgotPassword = { email -> viewModel.resetPassword(email) },
		onErrorShown = { viewModel.resetErrorState() }
	)
}

@Composable
fun ResetPasswordScreen(
	isLoading: Boolean,
	forgotPasswordValidator: AuthValidator.ResetPasswordValidator,
	isForgotPasswordSuccess: Boolean,
	requestError: String?,
	onBack: () -> Unit,
	onForgotPassword: (String?) -> Unit,
	onErrorShown: () -> Unit
) {
	var email: String? by remember { mutableStateOf(null) }
	val emailError =
		remember(forgotPasswordValidator) { mutableStateOf(forgotPasswordValidator.emailError) }

	AuthBackground(isLoading) {
		Box {
			if (!requestError.isNullOrEmpty()) {
				AppAlertDialog(requestError) { onErrorShown() }
			}
			Column(
				modifier = Modifier.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp)) {
					IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
						Icon(
							imageVector = Icons.Filled.ArrowBack,
							contentDescription = "cd_ic_back",
							tint = AppTheme.colors.glow
						)
					}
					Text(
						modifier = Modifier.align(Alignment.Center),
						text = stringResource(R.string.reset_password),
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.SemiBold,
						color = Color.White,
						textAlign = TextAlign.Center
					)
				}
				Spacer(modifier = Modifier.size(30.dp))
				Image(painter = painterResource(R.drawable.key), contentDescription = "cd_key")
				Spacer(modifier = Modifier.size(60.dp))
				if (isForgotPasswordSuccess) {
					Text(
						modifier = Modifier.padding(horizontal = 32.dp),
						text = stringResource(R.string.reset_password_success),
						style = MaterialTheme.typography.bodyLarge,
						color = Color.White,
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.size(32.dp))
					Text(
						modifier = Modifier.padding(horizontal = 20.dp),
						text = email ?: "",
						style =
						MaterialTheme.typography
							.customTextStyle(fontSize = 24.sp)
							.copy(
								shadow =
								Shadow(
									color = AppTheme.colors.glow,
									offset = Offset(2f, 2f),
									blurRadius = 50f
								)
							),
						color = Color.White,
						textAlign = TextAlign.Center
					)
				} else {
					Text(
						modifier = Modifier.padding(horizontal = 32.dp),
						text = stringResource(R.string.reset_password_description),
						style = MaterialTheme.typography.bodyMedium,
						color = AppTheme.colors.colorTextSecondaryVariant,
						textAlign = TextAlign.Center
					)
					Spacer(modifier = Modifier.size(24.dp))
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
						KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
					)
					Spacer(modifier = Modifier.size(16.dp))
					AuthButton(text = stringResource(R.string.send_reset_link)) { onForgotPassword(email) }
				}
			}
		}
	}
}
