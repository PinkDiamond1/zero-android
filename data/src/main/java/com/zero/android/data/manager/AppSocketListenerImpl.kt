package com.zero.android.data.manager

import com.zero.android.data.conversion.toEntity
import com.zero.android.database.dao.ChannelDao
import com.zero.android.database.dao.MessageDao
import com.zero.android.network.SocketListener
import com.zero.android.network.model.ApiChannel
import com.zero.android.network.model.ApiDirectChannel
import com.zero.android.network.model.ApiGroupChannel
import com.zero.android.network.model.ApiMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppSocketListenerImpl
@Inject
constructor(private val messageDao: MessageDao, private val channelDao: ChannelDao) :
	SocketListener {

	override fun onMessageReceived(channel: ApiChannel, message: ApiMessage) {
		CoroutineScope(Dispatchers.IO).launch {
			if (channel is ApiDirectChannel) channelDao.upsert(channel.toEntity())
			else if (channel is ApiGroupChannel) channelDao.upsert(channel.toEntity())
			messageDao.upsert(message.toEntity())
		}
	}

	override fun onMessageUpdated(channel: ApiChannel, message: ApiMessage) {
		CoroutineScope(Dispatchers.IO).launch {
			if (channel is ApiDirectChannel) channelDao.upsert(channel.toEntity())
			else if (channel is ApiGroupChannel) channelDao.upsert(channel.toEntity())
			messageDao.upsert(message.toEntity())
		}
	}

	override fun onMessageDeleted(channel: ApiChannel, msgId: String) {
		CoroutineScope(Dispatchers.IO).launch { messageDao.delete(msgId) }
	}
}
