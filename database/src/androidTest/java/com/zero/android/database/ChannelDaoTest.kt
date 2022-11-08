package com.zero.android.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zero.android.database.base.BaseDatabaseTest
import com.zero.android.database.model.fake.FakeEntity
import com.zero.android.database.util.result
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ChannelDaoTest : BaseDatabaseTest() {

	private val directChannel = FakeEntity.DirectChannelWithRefs(id = "directChannelId")
	private val groupChannel =
		FakeEntity.GroupChannelWithRefs(id = "groupChannelId", networkId = "networkId")

	@Before
	fun setup() = runTest { db.networkDao().insert(FakeEntity.NetworkEntity(id = "networkId")) }

	@Test
	fun insertDirectChannel() = runTest {
		channelDao.upsert(directChannel)
		channelDao.upsert(directChannel) // Checking 2nd insert

		val data = channelDao.getDirectChannel(directChannel.channel.id).first()
		assertEquals(directChannel.channel.id, data?.channel?.id)
		assertEquals(directChannel.lastMessage?.message?.id, data?.lastMessage?.message?.id)
		assertEquals(directChannel.channel.id, data?.lastMessage?.message?.channelId)
		assertEquals(directChannel.members.size, data?.members?.size)
	}

	@Test
	fun insertGroupChannel() = runTest {
		channelDao.upsert(groupChannel)
		channelDao.upsert(groupChannel) // Checking 2nd insert

		val data = channelDao.getGroupChannel(groupChannel.channel.id).first()
		assertEquals(groupChannel.channel.id, data?.channel?.id)
		assertEquals(groupChannel.lastMessage?.message?.id, data?.lastMessage?.message?.id)
		assertEquals(groupChannel.lastMessage?.author?.id, data?.lastMessage?.author?.id)
		assertEquals(groupChannel.channel.id, data?.lastMessage?.message?.channelId)
		assertEquals(groupChannel.members.size, data?.members?.size)
		assertEquals(groupChannel.operators.size, data?.operators?.size)
	}

	@Test
	fun getDirectChannels() = runTest {
		channelDao.upsert(directChannel)
		channelDao.upsert(FakeEntity.DirectChannelWithRefs(id = "directChannelId2", lastMessage = null))

		var data = channelDao.getDirectChannels().result()
		assertEquals("Empty channels", 1, data?.size)

		messageDao.upsert(
			FakeEntity.MessageWithRefs(
				id = "messageId2",
				channelId = "directChannelId2",
				reply = false
			),
			updateChannel = true
		)

		data = channelDao.getDirectChannels().result()
		assertEquals("Non empty channels", 2, data?.size)
	}

	@Test
	fun getGroupChannels() = runTest {
		channelDao.upsert(groupChannel)
		channelDao.upsert(FakeEntity.GroupChannelWithRefs(id = "groupChannelId2"))
		channelDao.upsert(
			FakeEntity.GroupChannelWithRefs(id = "groupChannelId3", category = "category2")
		)

		var data = channelDao.getGroupChannels("networkId").result()
		assertEquals(3, data?.size)

		data = channelDao.getGroupChannels("networkId", category = "category").result()
		assertEquals(2, data?.size)

		data = channelDao.getGroupChannels("networkId", category = "category2").result()
		assertEquals(1, data?.size)
	}

	@Test
	fun searchDirectChannels() = runTest {
		channelDao.upsert(directChannel)
		channelDao.upsert(FakeEntity.DirectChannelWithRefs(id = "directChannelId2", name = "Bot, Test"))
		channelDao.upsert(
			FakeEntity.DirectChannelWithRefs(id = "directChannelId2", name = "Two Member")
		)

		val data = channelDao.searchDirectChannels("Member").result()
		assertEquals(2, data?.size)
	}

	@Test
	fun searchGroupChannels() = runTest {
		channelDao.upsert(groupChannel)
		channelDao.upsert(
			FakeEntity.GroupChannelWithRefs(
				id = "groupChannelId2",
				networkId = "networkId",
				name = "Test"
			)
		)
		channelDao.upsert(
			FakeEntity.GroupChannelWithRefs(id = "groupChannelId3", networkId = "networkId")
		)

		val data = channelDao.searchGroupChannels("networkId", "group").result()
		assertEquals(2, data?.size)
	}

	@Test
	fun getUnreadDirectMessagesCount() = runTest {
		channelDao.upsert(
			directChannel.copy(channel = directChannel.channel.copy(unreadMessageCount = 3))
		)
		channelDao.upsert(
			directChannel.copy(
				channel = directChannel.channel.copy(id = "directChannelId2", unreadMessageCount = 2)
			)
		)

		val unreadCount = channelDao.getUnreadDirectMessagesCount().first()
		assertEquals(5, unreadCount)
	}

	@Test
	fun deleteDirectChannel() = runTest {
		channelDao.upsert(directChannel)
		channelDao.delete(directChannel.channel)

		assertNull(channelDao.getDirectChannel(directChannel.channel.id).firstOrNull())
		assertNull(messageDao.get(directChannel.lastMessage?.message?.id!!).firstOrNull())
	}

	@Test
	fun deleteGroupChannel() = runTest {
		channelDao.upsert(groupChannel)
		channelDao.delete(groupChannel.channel)

		assertNull(channelDao.getDirectChannel(groupChannel.channel.id).firstOrNull())
		assertNull(messageDao.get(groupChannel.lastMessage?.message?.id!!).firstOrNull())
	}
}
