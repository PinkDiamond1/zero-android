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
		val image: String? = null,
		val updated: Boolean = false,
		val nameError: Int? = null
	) {
		val isValid
			get() = nameError == null

		fun isChanged(channel: Channel): Boolean {
			return channel.name != name
		}

		fun validate(): EditChannelForm {
			var form = this
			if (name.isEmpty()) form = form.copy(nameError = R.string.name_required)
			return form
		}
	}

	private val channelId: String =
		checkNotNull(savedStateHandle[EditChannelDestination.ARG_CHANNEL_ID])
	private val isGroupChannel: Boolean =
		checkNotNull(savedStateHandle[EditChannelDestination.ARG_IS_GROUP_CHANNEL])

	private val _channel = MutableStateFlow<Channel?>(null)
	val channel = _channel.asStateFlow()

	internal val form = MutableStateFlow(EditChannelForm())

	init {
		loadChannel()
	}

	private fun loadChannel() {
		ioScope.launch {
			val request =
				if (isGroupChannel) {
					channelRepository.getGroupChannel(channelId)
				} else {
					channelRepository.getDirectChannel(channelId)
				}

			request.asResult().collect {
				if (it is Result.Success) {
					if (form.value.updated) {
						form.emit(form.value.copy(name = it.data.name, image = it.data.image, updated = false))

						_channel.emit(it.data)
					} else if (channel.value == null) {
						form.emit(form.value.copy(name = it.data.name, image = it.data.image))

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
					is DirectChannel -> channel.copy(name = form.value.name)
					is GroupChannel -> channel.copy(name = form.value.name)
					else -> throw IllegalStateException()
				}

			channelRepository.updateChannel(mChannel)
		}
	}

	fun onImagePicked(image: File) {
		viewModelScope.launch { form.emit(form.value.copy(image = image.path)) }
		ioScope.launch { channelRepository.updateChannelImage(channel.value!!, image) }
	}

	internal fun updateForm(updated: EditChannelForm) {
		viewModelScope.launch { form.emit(updated.validate()) }
	}
}
