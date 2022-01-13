package com.tech.riri.shared.cache

import com.squareup.sqldelight.db.SqlDriver

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class TextObjectDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}