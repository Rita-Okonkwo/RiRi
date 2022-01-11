package com.example.riri.shared.data.local

import com.example.riri.shared.data.models.TextObjectDatabaseModel

interface TextObjectInterface {
    fun addText(text: String)
    fun deleteText(id: Long)
    fun getTexts(): List<TextObjectDatabaseModel>
}