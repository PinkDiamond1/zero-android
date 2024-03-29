package com.zero.android.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zero.android.database.converter.DateConverters
import com.zero.android.database.converter.ListConverters
import com.zero.android.database.converter.ObjectConverters
import com.zero.android.database.dao.DirectChannelDaoImpl
import com.zero.android.database.dao.GroupChannelDaoImpl
import com.zero.android.database.dao.MemberDao
import com.zero.android.database.dao.MessageDaoImpl
import com.zero.android.database.dao.NetworkDao
import com.zero.android.database.dao.NotificationDao
import com.zero.android.database.dao.ProfileDao
import com.zero.android.database.dao.UserDao
import com.zero.android.database.migrations.MigrationSpec3to4
import com.zero.android.database.migrations.MigrationSpec4to5
import com.zero.android.database.model.ChannelEntity
import com.zero.android.database.model.ChannelMembersCrossRef
import com.zero.android.database.model.ChannelOperatorsCrossRef
import com.zero.android.database.model.MemberEntity
import com.zero.android.database.model.MessageEntity
import com.zero.android.database.model.MessageMentionCrossRef
import com.zero.android.database.model.MessageWithRefs
import com.zero.android.database.model.NetworkEntity
import com.zero.android.database.model.NetworkMembersCrossRef
import com.zero.android.database.model.NotificationEntity
import com.zero.android.database.model.ProfileEntity
import com.zero.android.database.model.UserEntity

@Database(
	entities =
	[
		UserEntity::class,
		ProfileEntity::class,
		NetworkEntity::class,
		MessageEntity::class,
		MemberEntity::class,
		ChannelEntity::class,
		NotificationEntity::class,
		NetworkMembersCrossRef::class,
		MessageMentionCrossRef::class,
		ChannelMembersCrossRef::class,
		ChannelOperatorsCrossRef::class
	],
	views = [MessageWithRefs::class],
	version = 6,
	autoMigrations =
	[
		AutoMigration(from = 2, to = 3),
		AutoMigration(from = 3, to = 4, spec = MigrationSpec3to4::class),
		AutoMigration(from = 4, to = 5, spec = MigrationSpec4to5::class),
		AutoMigration(from = 5, to = 6)
	]
)
@TypeConverters(DateConverters::class, ListConverters::class, ObjectConverters::class)
abstract class AppDatabase : RoomDatabase() {

	companion object {

		private const val DATABASE_NAME = "zero-database"

		@Volatile private var INSTANCE: AppDatabase? = null

		fun getInstance(context: Context): AppDatabase =
			INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

		private fun buildDatabase(context: Context) =
			Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
				.fallbackToDestructiveMigration()
				.build()
	}

	abstract fun userDao(): UserDao

	abstract fun profileDao(): ProfileDao

	abstract fun memberDao(): MemberDao

	abstract fun networkDao(): NetworkDao

	abstract fun directChannelDao(): DirectChannelDaoImpl

	abstract fun groupChannelDao(): GroupChannelDaoImpl

	abstract fun messageDao(): MessageDaoImpl

	abstract fun notificationDao(): NotificationDao
}
