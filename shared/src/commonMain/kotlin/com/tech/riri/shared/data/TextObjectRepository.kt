package com.tech.riri.shared.data

import com.tech.riri.shared.data.local.TextObjectInterface
import com.tech.riri.shared.data.local.TextObjectLocalDataSource
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.data.remote.TextObjectRemoteDataSource
import com.tech.riri.shared.entity.Image


class TextObjectRepository(private val remoteDataSource: TextObjectInterface, private val localDataSource: TextObjectInterface) {

    suspend fun addText(text: String) {
        localDataSource.addText(text)
    }


    suspend fun deleteText(textId: Long) {
        localDataSource.deleteText(textId)
    }

    suspend fun getTexts(): List<TextObjectDataModel> {
        return localDataSource.getTexts().map {
            TextObjectDataModel(it.audioText, it.id)
        }
    }

    suspend fun getResponse(apiKey: String, imageEndpoint: String, contentType: String) : Image {
        return remoteDataSource.getResponse(apiKey, imageEndpoint, contentType)
    }

    fun changeUrl(url : String) {
        remoteDataSource.changeUrl(url)
    }
}