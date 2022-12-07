package com.zero.android.database.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(tableName = "channels", columnName = "lastMessageId")
class MigrationSpec4to5 : AutoMigrationSpec
