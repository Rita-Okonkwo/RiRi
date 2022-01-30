package com.tech.riri.shared.data

import com.tech.riri.shared.data.local.TextObjectInterface
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.entity.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class TextObjectRepository(private val dispatcher: CoroutineDispatcher, private val remoteDataSource: TextObjectInterface, private val localDataSource: TextObjectInterface) :
    TextObjectRepositoryInterface {

    override suspend fun addText(text: String) {
        withContext(dispatcher) {
            localDataSource.addText(text)
        }
    }


    override suspend fun deleteText(textId: Long) {
        withContext(dispatcher) {
            localDataSource.deleteText(textId)
        }
    }

    override suspend fun getTexts(): List<TextObjectDataModel> {
        return withContext(dispatcher) {
            localDataSource.getTexts().map {
                TextObjectDataModel(it.audioText, it.id)
            }
        }
    }

    override suspend fun getResponse(apiKey: String, imageEndpoint: String, contentType: String) : Image {
        return withContext(dispatcher) {
            remoteDataSource.getResponse(apiKey, imageEndpoint, contentType)
        }
    }

    override fun changeUrl(url : String) {
        remoteDataSource.changeUrl(url)
    }
}