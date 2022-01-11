package com.tech.riri.androidApp.textList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.data.TextObjectRepository
import com.tech.riri.shared.data.local.TextSqlDelightDatabase
import com.tech.riri.shared.data.models.TextObjectDataModel

class TextListViewModel(application: Application) : AndroidViewModel(application) {
    private val textObjectRepository =
        TextObjectRepository(TextSqlDelightDatabase(TextObjectDatabaseDriverFactory(application.applicationContext)))

    fun getTextList(): List<TextObjectDataModel> {
        return textObjectRepository.getTexts()
    }

    fun deleteText(textObjectDataModel: TextObjectDataModel) {
        textObjectRepository.deleteText(textObjectDataModel.id)
    }
}