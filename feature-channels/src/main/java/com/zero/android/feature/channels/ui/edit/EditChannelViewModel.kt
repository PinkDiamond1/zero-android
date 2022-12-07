package com.zero.android.feature.channels.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zero.android.common.R
import com.zero.android.common.ui.Result
import com.zero.android.common.ui.asResult
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.repository.ChannelRepository
import com.zero.android.feature.channels.navigation.EditChannelDestination
import com.zero.android.models.Channel
import com.zero.android.models.DirectChannel
import com.zero.android.models.GroupChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditChannelViewModel
@Inject
constructor(savedStateHandle: SavedStateHandle, private val channelRepository: ChannelRepository) :
	BaseViewModel() {

	internal data class EditChannelForm(
		val name: String = "",
		val description: String = "",
		val image: String? = null,
		val updated: Boolean = false,
		val nameError: Int? = null,
		val descriptionError: Int? = null
	) {
		val isValid
			get() = nameError == null && descriptionError == null

		fun isChanged(channel: Channel): Boolean {
			return channel.name != name || channel.description != description
		}

		fun validate(): EditChannelForm {
			var form = this
			if (name.isEmpty()) form = form.copy(nameError = R.string.name_required)
			return form
		}

		fun map(channel: Channel) =
			copy(name = channel.name, description = channel.description ?: "", image = channel.image)
	}

	private val channelId: String =
		checkNotNull(savedStateHandle[EditChannelDestination.ARG_CHANNEL_ID])

	private val _channel = MutableStateFlow<Channel?>(null)
	val channel = _channel.asStateFlow()

	internal val form = MutableStateFlow(EditChannelForm())
	internal val uploadingImage = MutableStateFlow(false)

	init {
		loadChannel()
	}

	private fun loadChannel() {
		ioScope.launch {
			channelRepository.getChannel(channelId).asResult().collect {
				if (it is Result.Success) {
					if (form.value.updated) {
						form.emit(form.value.map(it.data).copy(updated = false))
						_channel.emit(it.data)
					} else if (channel.value == null) {
						form.emit(form.value.map(it.data))
						_channel.emit(it.data)
					}
				}
			}
		}
	}

	fun updateChannel() {
		if (!form.value.isChanged(channel.value!!) || !form.value.isValid) return
		ioScope.launch {
			form.emit(form.value.copy(updated = true))
			val mChannel =
				when (val channel = channel.value!!) {
					is DirectChannel ->
						channel.copy(name = form.value.name, description = form.value.description)
					is GroupChannel ->
						channel.copy(name = form.value.name, description = form.value.description)
					else -> throw IllegalStateException()
				}

			channelRepository.updateChannel(mChannel)
		}
	}

	fun onImagePicked(image: File) {
		viewModelScope.launch { form.emit(form.value.copy(image = image.path)) }
		ioScope.launch {
			uploadingImage.emit(true)
			channelRepository.updateChannelImage(channel.value!!, image)
			uploadingImage.emit(false)
		}
	}

	internal fun updateForm(updated: EditChannelForm) {
		viewModelScope.launch { form.emit(updated.validate()) }
	}
}
