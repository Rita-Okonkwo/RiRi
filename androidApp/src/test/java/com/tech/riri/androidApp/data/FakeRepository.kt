package com.tech.riri.androidApp.data

import com.tech.riri.shared.data.TextObjectRepositoryInterface
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.entity.AnalyzeResult
import com.tech.riri.shared.entity.Image

class FakeRepository : TextObjectRepositoryInterface {
    var database: MutableMap<Long, String> = mutableMapOf()
    private var count = 1L
    override suspend fun addText(text: String) {
        if (text.isEmpty()) {
            return
        }
        database[count] = text
        count += 1
    }

    override suspend fun deleteText(textId: Long) {
        database.remove(textId)
    }

    override suspend fun getTexts(): List<TextObjectDataModel> {
        val list: MutableList<TextObjectDataModel> = mutableListOf()
        database.forEach { (l, s) -> list.add(TextObjectDataModel(s, l)) }
        return list
    }

    override suspend fun getResponse(
        apiKey: String,
        imageEndpoint: String,
        contentType: String
    ): Image {
        if (apiKey.isEmpty() or imageEndpoint.isEmpty() or contentType.isEmpty()) {
            return Image("", "", "", null)
        }
        return Image("200", "10", "20", AnalyzeResult(mutableListOf(), "1"))
    }

    override fun changeUrl(url: String) {
        //not necessary
    }
}