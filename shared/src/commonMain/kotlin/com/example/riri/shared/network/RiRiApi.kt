package com.example.riri.shared.network

import com.example.riri.shared.entity.Image
import com.example.riri.shared.entity.Url
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*

class RiRiApi {
    var operationLocationUrl : String? = ""
    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    private suspend fun sendImage(url : String) : HttpResponse {
        return httpClient.post(IMAGE_ENDPOINT) {
            headers {
                append("Content-Type", CONTENT_TYPE)
                append("Ocp-Apim-Subscription-Key", OCP_APIM)
            }
            body = Url(url)
        }
    }

    suspend fun getResponse() : Image {
        if (operationLocationUrl == "") {
            kotlin.runCatching {
                sendImage("https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Atomist_quote_from_Democritus.png/338px-Atomist_quote_from_Democritus.png")
            }.onSuccess {
                operationLocationUrl = it.headers.get("Operation-Location")
                println(operationLocationUrl)

            }.onFailure {
                print(it.message)
            }
        }
        return retrieveImageResponse(operationLocationUrl)
    }

    suspend fun retrieveImageResponse(url: String?): Image {
        return httpClient.get(url!!) {
            headers {
                append("Content-Type", CONTENT_TYPE)
                append("Ocp-Apim-Subscription-Key", OCP_APIM)
            }
            contentType(ContentType.Application.Json)
        }
    }

    companion object {
        private const val IMAGE_ENDPOINT = "https://ririvision.cognitiveservices.azure.com/vision/v3.1/read/analyze/"
        private const val CONTENT_TYPE = "application/json"
        private const val OCP_APIM = "80d2b8c43b52442fad01d26a99e63ce7"
    }
}

