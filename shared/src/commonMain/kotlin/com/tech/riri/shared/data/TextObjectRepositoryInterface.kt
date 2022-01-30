package com.tech.riri.shared.data

import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.entity.Image

interface TextObjectRepositoryInterface {
    suspend fun addText(text: String)

    suspend fun deleteText(textId: Long)

    suspend fun getTexts(): List<TextObjectDataModel>

    suspend fun getResponse(apiKey: String, imageEndpoint: String, contentType: String) : Image

    fun changeUrl(url:String)
}