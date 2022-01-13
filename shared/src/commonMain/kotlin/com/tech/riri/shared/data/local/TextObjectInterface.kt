package com.tech.riri.shared.data.local

import com.tech.riri.shared.data.models.TextObjectDatabaseModel

interface TextObjectInterface {
    fun addText(text: String)
    fun deleteText(id: Long)
    fun getTexts(): List<TextObjectDatabaseModel>
}