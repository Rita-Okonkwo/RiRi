package com.tech.riri.shared.data.local

import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.cache.TextObjectSqlDelightDatabase
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.entity.Image

class TextObjectLocalDataSource(textObjectDatabaseDriverFactory: TextObjectDatabaseDriverFactory) :
    TextObjectInterface {
    private val database =
        TextObjectSqlDelightDatabase.invoke(textObjectDatabaseDriverFactory.createDriver())
    private val textObjectQueries = database.textObjectSqlDelightDatabaseQueries
    override suspend fun addText(text: String) {
        textObjectQueries.insertText(text)
    }

    override suspend fun deleteText(id: Long) {
        textObjectQueries.deleteText(id)
    }

    override suspend fun getTexts(): List<TextObjectDataModel> {
        return textObjectQueries.getText().executeAsList().map {
            TextObjectDataModel(it.text, it.id)
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