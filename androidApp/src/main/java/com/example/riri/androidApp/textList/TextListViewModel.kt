package com.example.riri.androidApp.textList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.example.riri.shared.data.TextObjectRepository
import com.example.riri.shared.data.local.TextSqlDelightDatabase
import com.example.riri.shared.data.models.TextObjectDataModel

class TextListViewModel(application: Application) : AndroidViewModel(application) {
    private val textObjectRepository =
        TextObjectRepository(TextSqlDelightDatabase(TextObjectDatabaseDriverFactory(application.applicationContext)))

    fun getTextList(): List<TextObjectDataModel> {
        return textObjectRepository.getTexts()
    }
}