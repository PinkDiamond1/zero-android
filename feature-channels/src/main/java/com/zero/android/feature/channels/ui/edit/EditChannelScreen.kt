package com.zero.android.feature.channels.ui.edit

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.zero.android.common.R
import com.zero.android.common.extensions.getActivity
import com.zero.android.common.extensions.toFile
import com.zero.android.feature.channels.ui.edit.EditChannelViewModel.EditChannelForm
import com.zero.android.models.Channel
import com.zero.android.models.fake.FakeModel
import com.zero.android.ui.components.AppBar
import com.zero.android.ui.components.CircularInitialsImage
import com.zero.android.ui.components.CircularProgress
import com.zero.android.ui.components.LoadingContainer
import com.zero.android.ui.components.form.TextField
import com.zero.android.ui.extensions.bodyPaddings
import com.zero.android.ui.manager.GalleryManager
import com.zero.android.ui.theme.AppTheme
import com.zero.android.ui.util.Preview
import java.io.File

@Composable
fun EditChannelRoute(viewModel: EditChannelViewModel = hiltViewModel(), onBackClick: () -> Unit) {
	val channel by viewModel.channel.collectAsState()
	val form by viewModel.form.collectAsState()
	val uploadingImage by viewModel.uploadingImage.collectAsState()

	EditChannelScreen(
		channel = channel,
		form = form,
		uploadingImage = uploadingImage,
		updateForm = { viewModel.updateForm(it) },
		updateChannel = { viewModel.updateChannel() },
		onImagePicked = { viewModel.onImagePicked(it) },
		onBackClick = onBackClick
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditChannelScreen(
	channel: Channel?,
	form: EditChannelForm,
	uploadingImage: Boolean,
	updateForm: (EditChannelForm) -> Unit,
	updateChannel: () -> Unit,
	onImagePicked: (File) -> Unit,
	onBackClick: () -> Unit
) {
	val context = LocalContext.current
	val isLoading = channel == null

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
					onImagePicked(file)
				}
			}
		)

	val topBar: @Composable () -> Unit = {
		AppBar(
			centered = true,
			navIcon = {
				IconButton(onClick = { onBackClick() }) {
					Icon(
						imageVector = Icons.Filled.ArrowBack,
						contentDescription = "cd_back",
						tint = AppTheme.colors.glow
					)
				}
			},
			title = {
				Text(
					text = stringResource(R.string.edit_channel),
					style = MaterialTheme.typography.displayLarge
				)
			},
			actions = {
				if (channel != null && form.isChanged(channel)) {
					TextButton(
						colors = ButtonDefaults.textButtonColors(contentColor = AppTheme.colors.glow),
						onClick = updateChannel
					) {
						Text(text = stringResource(R.string.update), fontSize = 12.sp)
					}
				}
			}
		)
	}

	val channelImage: @Composable ColumnScope.() -> Unit = {
		Column(
			modifier = Modifier.align(Alignment.CenterHorizontally),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Box {
				CircularInitialsImage(
					modifier =
					Modifier.clickable {
						context.getActivity()?.let { activity ->
							GalleryManager.getGalleryImagePicker(activity) {
								imageSelectorLauncher.launch(it)
							}
						}
					},
					size = 80.dp,
					name = channel?.name ?: "",
					url = form.image
				)

				if (uploadingImage) {
					CircularProgress(modifier = Modifier.align(Alignment.Center), size = 50.dp)
				}
			}
		}
	}

	val channelFields: @Composable ColumnScope.() -> Unit = {
		TextField(
			modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
			value = form.name,
			label = stringResource(R.string.name),
			focusedByDefault = true,
			error = form.nameError,
			keyboardOptions =
			KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
			onTextChanged = { updateForm(form.copy(name = it, nameError = null)) },
			onKeyboardAction = { updateChannel() }
		)

		TextField(
			modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
			value = form.description,
			label = stringResource(R.string.description),
			error = form.descriptionError,
			singleLine = false,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
			onTextChanged = { updateForm(form.copy(description = it, descriptionError = null)) }
		)
	}

	Scaffold(topBar = { topBar() }) { innerPaddings ->
		Box(Modifier.padding(innerPaddings)) {
			LoadingContainer(loading = isLoading) {
				Column(
					modifier =
					Modifier.verticalScroll(rememberScrollState()).fillMaxWidth().bodyPaddings()
				) {
					channelImage()
					channelFields()
				}
			}
		}
	}
}

@Preview
@Composable
private fun EditChannelScreenPreview() = Preview {
	val channel = FakeModel.GroupChannel()
	EditChannelScreen(
		channel = channel,
		form = EditChannelForm(name = channel.name, image = channel.image),
		uploadingImage = false,
		updateForm = {},
		updateChannel = {},
		onImagePicked = {},
		onBackClick = {}
	)
}
