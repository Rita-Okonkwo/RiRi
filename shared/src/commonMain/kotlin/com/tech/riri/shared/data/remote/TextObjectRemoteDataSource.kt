package com.tech.riri.shared.data.remote

import com.tech.riri.shared.data.local.TextObjectInterface
import com.tech.riri.shared.data.models.TextObjectDataModel
import com.tech.riri.shared.entity.Image
import com.tech.riri.shared.entity.Url
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*


class TextObjectRemoteDataSource : TextObjectInterface {
    var operationLocationUrl: String? = ""
    private var imageUrl = ""
    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    private suspend fun sendImage(url: String, apiKey: String, imageEndpoint: String, contentType: String): HttpResponse {
        return httpClient.post(imageEndpoint) {
            headers {
                append("Content-Type", contentType)
                append("Ocp-Apim-Subscription-Key", apiKey)
            }
            body = Url(url)
        }
    }

    override suspend fun addText(text: String) {
        //not required
    }

    override suspend fun deleteText(id: Long) {
        //not required
    }

    override suspend fun getTexts(): List<TextObjectDataModel> {
        //not required
        return emptyList()
    }

    override suspend fun getResponse(apiKey: String, imageEndpoint: String, contentType: String): Image {
        if (operationLocationUrl == "") {
            kotlin.runCatching {
                print(imageUrl)
                sendImage(imageUrl, apiKey, imageEndpoint, contentType)
            }.onSuccess {
                operationLocationUrl = it.headers.get("Operation-Location")
                println(operationLocationUrl)

            }.onFailure {
                print(it.message)
            }
        }
        return retrieveImageResponse(operationLocationUrl, apiKey, imageEndpoint, contentType)
    }

    private suspend fun retrieveImageResponse(url: String?, apiKey: String, imageEndpoint: String, contentType: String ): Image {
        return httpClient.get(url!!) {
            headers {
                append("Content-Type", contentType)
                append("Ocp-Apim-Subscription-Key", apiKey)
            }
            contentType(ContentType.Application.Json)
        }
    }

    override fun changeUrl(url: String) {
        imageUrl = url
    }
}

