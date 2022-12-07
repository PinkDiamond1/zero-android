package com.zero.android.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zero.android.database.base.BaseDatabaseTest
import com.zero.android.database.model.NotificationEntity
import com.zero.android.database.util.result
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NotificationDaoTest : BaseDatabaseTest() {

	private val notification =
		NotificationEntity(id = "notificationId", createdAt = Instant.DISTANT_FUTURE)

	@Test
	fun insertNotification() = runTest {
		db.notificationDao().upsert(notification)
		db.notificationDao().upsert(notification)

		val data = db.notificationDao().get(notification.id).first()
		assertEquals(notification.id, data?.id)
	}

	@Test
	fun getNotifications() = runTest {
		db.notificationDao().upsert(notification)
		db.notificationDao().upsert(notification.copy(id = "notificationId2"))

		val data = db.notificationDao().getAll().result()
		assertEquals(2, data?.size)
	}

	@Test
	fun deleteNotification() = runTest {
		db.notificationDao().upsert(notification)
		db.notificationDao().delete(notification)

		assertNull(db.notificationDao().get(notification.id).firstOrNull())
		assertTrue(db.notificationDao().getAll().result()?.size == 0)
	}
}
