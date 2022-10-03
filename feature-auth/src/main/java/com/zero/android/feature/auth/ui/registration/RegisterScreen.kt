package com.zero.android.feature.auth.ui.registration

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.common.extensions.getActivity
import com.zero.android.common.extensions.toFile
import com.zero.android.feature.auth.AuthViewModel
import com.zero.android.feature.auth.ui.components.AuthBackground
import com.zero.android.feature.auth.ui.components.AuthButton
import com.zero.android.feature.auth.ui.components.AuthInputField
import com.zero.android.feature.auth.ui.components.PasswordStrengthMeter
import com.zero.android.feature.auth.ui.components.PasswordTextField
import com.zero.android.feature.auth.util.AuthValidator
import com.zero.android.ui.components.AppAlertDialog
import com.zero.android.ui.manager.GalleryManager
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.BackHandler
import java.io.File

@Composable
fun RegisterRoute(viewModel: AuthViewModel = hiltViewModel(), onBack: () -> Unit) {
	val registerValidator by viewModel.registerValidator.collectAsState()
	val isLoading: Boolean by viewModel.loading.collectAsState()
	val requestError: String? by viewModel.error.collectAsState()
	var profilePic: File? by remember { mutableStateOf(null) }

	DisposableEffect(Unit) { onDispose { viewModel.resetErrorState(resetValidations = true) } }

	BackHandler { onBack() }
	val context = LocalContext.current
	val imageSelectorLauncher =
		rememberLauncherForActivityResult(
			contract = ActivityResultContracts.StartActivityForResult(),
			onResult = {
				val resultCode = it.resultCode
				val data = it.data
				if (resultCode == Activity.RESULT_OK) {
					// Image Uri will not be null for RESULT_OK
					val fileUri = data?.data!!
					val file =
						try {
							fileUri.toFile()
						} catch (e: Exception) {
							fileUri.toFile(context)
						}
					profilePic = file
				}
			}
		)
	RegisterScreen(
		isLoading,
		registerValidator,
		requestError,
		onBack = onBack,
		onPickImage = { imageSelectorLauncher.launch(it) },
		onRegister = { name, email, password, confirmPassword ->
			viewModel.register(name, email, password, confirmPassword, profilePic)
		},
		onErrorShown = { viewModel.resetErrorState() }
	)
}

@Composable
fun RegisterScreen(
	isLoading: Boolean,
	registerValidator: AuthValidator.RegistrationValidator,
	requestError: String?,
	onBack: () -> Unit,
	onPickImage: (Intent) -> Unit,
	onRegister: (String?, String?, String?, String?) -> Unit,
	onErrorShown: () -> Unit
) {
	val context = LocalContext.current

	var name: String? by remember { mutableStateOf(null) }
	var email: String? by remember { mutableStateOf(null) }
	var password: String? by remember { mutableStateOf(null) }
	var confirmPassword: String? by remember { mutableStateOf(null) }
	val nameError = remember(registerValidator) { mutableStateOf(registerValidator.nameError) }
	val emailError = remember(registerValidator) { mutableStateOf(registerValidator.emailError) }
	val passwordError =
		remember(registerValidator) { mutableStateOf(registerValidator.passwordError) }
	val confirmPasswordError =
		remember(registerValidator) { mutableStateOf(registerValidator.confirmPasswordError) }

	var showPasswordMeter by remember { mutableStateOf(false) }

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
						text = stringResource(R.string.create_account),
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.SemiBold,
						color = AppTheme.colors.colorTextPrimary,
						textAlign = TextAlign.Center
					)
				}
				Spacer(modifier = Modifier.size(24.dp))
				OutlinedButton(
					modifier = Modifier.size(100.dp),
					onClick = {
						context.getActivity()?.let { GalleryManager.getGalleryImagePicker(it, onPickImage) }
					},
					shape = CircleShape,
					border = BorderStroke(1.dp, AppTheme.colors.glow)
				) {
					Icon(
						imageVector = Icons.Outlined.PhotoCamera,
						contentDescription = "cd_add_image",
						tint = AppTheme.colors.glow
					)
				}
				Spacer(modifier = Modifier.size(24.dp))
				AuthInputField(
					modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
					placeHolder = { Text(stringResource(R.string.member_name)) },
					onTextChanged = {
						name = it
						nameError.value = null
					},
					error = nameError.value,
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
				)
				Spacer(modifier = Modifier.size(16.dp))
				AuthInputField(
					modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
					placeHolder = { Text(stringResource(R.string.email_id)) },
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
					},
					onFocusChanged = { showPasswordMeter = it },
					imeAction = ImeAction.Next
				)
				if (showPasswordMeter && !password.isNullOrEmpty()) {
					Spacer(modifier = Modifier.size(8.dp))
					PasswordStrengthMeter(
						modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
						password = password!!
					)
				}
				Spacer(modifier = Modifier.size(16.dp))
				PasswordTextField(
					modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
					placeHolder = { Text(stringResource(R.string.confirm_password)) },
					onTextChanged = {
						confirmPassword = it
						confirmPasswordError.value = null
					},
					error = confirmPasswordError.value,
					imeAction = ImeAction.Done
				)
				Spacer(modifier = Modifier.size(24.dp))
				AuthButton(text = stringResource(R.string.create_account)) {
					onRegister(name, email, password, confirmPassword)
				}
			}
		}
	}
}
