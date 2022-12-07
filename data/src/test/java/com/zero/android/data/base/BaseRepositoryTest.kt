package com.zero.android.data.base

import com.zero.android.database.dao.ChannelDao
import com.zero.android.database.dao.DirectChannelDaoImpl
import com.zero.android.database.dao.GroupChannelDaoImpl
import com.zero.android.database.dao.MemberDao
import com.zero.android.database.dao.MessageDao
import com.zero.android.database.dao.MessageDaoImpl
import org.mockito.Mockito

abstract class BaseRepositoryTest {

	protected val mockDirectChannelDao = Mockito.mock(DirectChannelDaoImpl::class.java)
	protected val mockGroupChannelDao = Mockito.mock(GroupChannelDaoImpl::class.java)
	protected val mockMemberDao = Mockito.mock(MemberDao::class.java)
	protected val mockMessageDaoImpl = Mockito.mock(MessageDaoImpl::class.java)
	protected val mockMessageDao =
		MessageDao(
			messageDao = mockMessageDaoImpl,
			memberDao = mockMemberDao,
			channelDao = mockDirectChannelDao
		)
	protected val mockChannelDao =
		ChannelDao(
			channelDao = mockGroupChannelDao,
			directChannelDao = mockDirectChannelDao,
			groupChannelDao = mockGroupChannelDao,
			memberDao = mockMemberDao,
			messageDao = mockMessageDao
		)
}
