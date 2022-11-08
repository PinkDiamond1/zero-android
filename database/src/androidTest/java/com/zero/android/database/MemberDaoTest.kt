package com.zero.android.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zero.android.database.base.BaseDatabaseTest
import com.zero.android.database.model.MemberEntity
import com.zero.android.database.model.fake.FakeEntity
import com.zero.android.database.util.result
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MemberDaoTest : BaseDatabaseTest() {

	private val member = MemberEntity(id = "memberId")

	@Before
	fun setup() = runTest {
		db.networkDao().insert(FakeEntity.NetworkEntity(id = "networkId"))
		db.networkDao().insert(FakeEntity.NetworkEntity(id = "networkId2"))
	}

	@Test
	fun insertMember() = runTest {
		db.memberDao().insert(member)
		db.memberDao().insert(member) // Checking 2nd insert

		assertNotNull(db.memberDao().get(member.id))
	}

	@Test
	fun insertMemberByNetwork() = runTest {
		db.memberDao().upsert(member)
		db.memberDao().upsert("networkId", listOf(MemberEntity("memberId2")))
		db.memberDao().upsert("networkId2", listOf(MemberEntity("memberId3")))

		assertNotNull(db.memberDao().get(member.id))
		assertNotNull(db.memberDao().get("memberId2"))

		var data = db.memberDao().getByNetwork("networkId").result()
		assertEquals(1, data?.size)
		assertEquals("memberId2", data!![0].id)

		data = db.memberDao().getByNetwork("networkId2").result()
		assertEquals(1, data?.size)
		assertEquals("memberId3", data!![0].id)
	}

	@Test
	fun updateMember() = runTest {
		db.memberDao().insert(member)
		db.memberDao().update(MemberEntity(id = member.id, name = "Name"))

		val data = db.memberDao().get(member.id).first()
		assertEquals("Name", data?.name)
	}

	@Test
	fun deleteMember() = runTest {
		db.memberDao().insert(member)
		db.memberDao().delete(member)

		assertNull(db.memberDao().get(member.id).firstOrNull())
	}
}
