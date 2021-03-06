package com.tech.riri.shared.data.local

import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.entity.Image

interface TextObjectInterface {
    suspend fun addText(text: String)
    suspend fun deleteText(id: Long)
    suspend fun getTexts(): List<TextObjectDataModel>
    suspend fun getResponse(apiKey: String, imageEndpoint: String, contentType: String) : Image
    fun changeUrl(url : String)
}