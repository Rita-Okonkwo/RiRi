package com.tech.riri.shared.cache

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver


actual class TextObjectDatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TextObjectSqlDelightDatabase.Schema, context, "textobject.db")
    }
}
