package com.example.riri.shared.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Url(
        @SerialName("url")
        val url: String
)

@Serializable
data class Image(
        @SerialName("status")
        var status: String,
        @SerialName("createdDateTime")
        val createdDateTime: String,
        @SerialName("lastUpdatedDateTime")
        val lastUpdatedDateTime: String,
        @SerialName("analyzeResult")
        val analyzeResult: AnalyzeResult? = null
)

@Serializable
data class AnalyzeResult(
        @SerialName("readResults")
        val readResults: List<ReadResult>,
        @SerialName("version")
        val version: String
)

@Serializable
data class ReadResult(
        @SerialName("angle")
        val angle: Double,
        @SerialName("height")
        val height: Int,
        @SerialName("lines")
        val lines: List<Line>,
        @SerialName("page")
        val page: Int,
        @SerialName("unit")
        val unit: String,
        @SerialName("width")
        val width: Int
)

@Serializable
data class Line(
        @SerialName("boundingBox")
        val boundingBox: List<Int>,
        @SerialName("text")
        val text: String,
        @SerialName("words")
        val words: List<Word>,
        @SerialName("appearance")
        val appearance: Appearance? = null
)

@Serializable
data class Appearance(
        @SerialName("style")
        val style: Style
)

@Serializable
data class Word(
        @SerialName("boundingBox")
        val boundingBox: List<Int>,
        @SerialName("confidence")
        val confidence: Double,
        @SerialName("text")
        val text: String
)

@Serializable
data class Style(
        @SerialName("confidence")
        val confidence: Double,
        @SerialName("name")
        val name: String
)