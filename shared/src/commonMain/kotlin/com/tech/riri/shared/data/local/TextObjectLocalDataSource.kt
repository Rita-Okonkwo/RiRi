package com.tech.riri.shared.data.local

import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.cache.TextObjectSqlDelightDatabase
import com.tech.riri.shared.data.models.TextObjectDatabaseModel
import com.tech.riri.shared.entity.Image

class TextObjectLocalDataSource(textObjectDatabaseDriverFactory: TextObjectDatabaseDriverFactory) :
    TextObjectInterface {
    val database =
        TextObjectSqlDelightDatabase.invoke(textObjectDatabaseDriverFactory.createDriver())
    val textObjectQueries = database.textObjectSqlDelightDatabaseQueries
    override suspend fun addText(text: String) {
        textObjectQueries.insertText(text)
    }

    override suspend fun deleteText(id: Long) {
        textObjectQueries.deleteText(id)
    }

    override suspend fun getTexts(): List<TextObjectDatabaseModel> {
        return textObjectQueries.getText().executeAsList().map {
            TextObjectDatabaseModel(it.text, it.id)
        }
    }

    override suspend fun getResponse(apiKey: String, imageEndpoint: String, contentType: String): Image {
        //not required
        return Image("", "", "", null)
    }

    override fun changeUrl(url: String) {
        //not required
    }
}