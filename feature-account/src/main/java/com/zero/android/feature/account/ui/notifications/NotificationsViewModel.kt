package com.zero.android.feature.account.ui.notifications

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zero.android.common.ui.base.BaseViewModel
import com.zero.android.data.repository.NotificationRepository
import com.zero.android.models.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel
@Inject
constructor(private val notificationRepository: NotificationRepository) : BaseViewModel() {

	private val _notifications = MutableStateFlow<PagingData<Notification>>(PagingData.empty())
	val notifications = _notifications.asStateFlow()

	private val _loading = MutableStateFlow(false)
	val loading = _loading.asStateFlow()

	init {
		loadNotifications()
	}

	private fun loadNotifications() {
		_loading.value = true
		ioScope.launch {
			notificationRepository
				.getNotifications()
				.cachedIn(viewModelScope)
				.onEach { _loading.emit(false) }
				.collect(_notifications)
		}
	}
}
