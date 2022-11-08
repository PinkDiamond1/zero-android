package com.zero.android.database.migrations

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

@RenameColumn(tableName = "channels", fromColumnName = "coverUrl", toColumnName = "image")
class MigrationSpec3to4 : AutoMigrationSpec
