package com.tech.riri.shared.data

import com.tech.riri.shared.data.local.TextObjectInterface
import com.tech.riri.shared.data.local.TextObjectLocalDataSource
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.data.remote.TextObjectRemoteDataSource
import com.tech.riri.shared.entity.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TextObjectRepository(private val dispatcher: CoroutineDispatcher, private val remoteDataSource: TextObjectInterface, private val localDataSource: TextObjectInterface) {

    suspend fun addText(text: String) {
        withContext(dispatcher) {
            localDataSource.addText(text)
        }
    }


    suspend fun deleteText(textId: Long) {
        withContext(dispatcher) {
            localDataSource.deleteText(textId)
        }
    }

    suspend fun getTexts(): List<TextObjectDataModel> {
        return withContext(dispatcher) {
            localDataSource.getTexts().map {
                TextObjectDataModel(it.audioText, it.id)
            }
        }
    }

    suspend fun getResponse(apiKey: String, imageEndpoint: String, contentType: String) : Image {
        return withContext(dispatcher) {
            remoteDataSource.getResponse(apiKey, imageEndpoint, contentType)
        }
    }

    fun changeUrl(url : String) {
        remoteDataSource.changeUrl(url)
    }
}