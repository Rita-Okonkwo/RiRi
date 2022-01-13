package com.tech.riri.shared.cache

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class TextObjectDatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TextObjectSqlDelightDatabase.Schema, "textobject.db")
    }
}